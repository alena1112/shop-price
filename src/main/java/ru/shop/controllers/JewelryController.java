package ru.shop.controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.shop.JewelryShopProperties;
import ru.shop.SPException;
import ru.shop.controllers.dto.JewelryDto;
import ru.shop.controllers.dto.JewelryMaterialDto;
import ru.shop.controllers.dto.JewelryMaterialsDto;
import ru.shop.model.JewelryMaterial;
import ru.shop.model.Material;
import ru.shop.service.JewelryMaterialService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(
        value = {"/jewelry"},
        produces = {"application/json"}
)
@RequiredArgsConstructor
public class JewelryController {
    private static final Logger log = LoggerFactory.getLogger(JewelryController.class);

    private final RestTemplate restTemplate;
    private final JewelryShopProperties jewelryShopProperties;
    private final JewelryMaterialService jewelryMaterialService;

    private List<JewelryDto> allJewelries;//TODO сделать норм кэш

    @GetMapping(value = "/all")
    public RestResponse<List<JewelryDto>> loadJewelries() {
        log.info("request to get all jewelries");
        return RestResponse.withData(getAllJewelries());
    }

    @GetMapping(value = "/{id}")
    public RestResponse<JewelryDto> getJewelry(@PathVariable("id") Long jewelryId) {
        log.info("request to get a jewelry by id {}", jewelryId);
        JewelryDto jewelryDto = getAllJewelries().stream().filter(j -> j.getId().equals(jewelryId)).findFirst().orElse(null);
        if (jewelryDto != null) {
            jewelryDto.setMaterials(jewelryMaterialService.getMaterialsByJewelryId(jewelryId)
                    .stream().map(m -> new JewelryMaterialDto(m.getMaterialId(), m.getNumber(),
                            m.getMaterial().getImageURL(), m.getMaterial().getName()))
                    .collect(Collectors.toList())
            );
        }
        return RestResponse.withData(jewelryDto);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public RestResponse<?> saveJewelry(@RequestBody JewelryMaterialsDto jewelryMaterialsDto) {
        log.info("request to save all jewelry materials");
        SPException.checkNotNull(jewelryMaterialsDto.getJewelryId(), "Jewelry Id is null!");

        List<JewelryMaterial> result = jewelryMaterialsDto.getMaterials().stream()
                .map(dto -> {
                    if (dto.getCount() == null || dto.getCount() <= 0) {
                        throw new SPException("Materials count must be positive number for material %s!");
                    }
                    return new JewelryMaterial(
                            jewelryMaterialsDto.getJewelryId(),
                            dto.getId(),
                            new Material(dto.getId()),
                            dto.getCount());
                })
                .collect(Collectors.toList());
        jewelryMaterialService.saveJewelryMaterials(jewelryMaterialsDto.getJewelryId(), result);
        return RestResponse.ok();
    }

    private List<JewelryDto> getAllJewelries() {
        if (allJewelries == null) {
            log.info("start loading jewelries from jewelry shop service");
            String address = jewelryShopProperties.getAddress() + jewelryShopProperties.getAllJewelryUrl();
            try {
                ResponseEntity<List<JewelryDto>> responseEntity =
                        restTemplate.exchange(
                                address,
                                HttpMethod.GET,
                                null,
                                new ParameterizedTypeReference<>() {
                                }
                        );
                allJewelries = responseEntity.getBody();
            } catch (Exception e) {
                throw new SPException("Could not load jewelries from Shop Service");
            }
            if (allJewelries != null) {
                log.info("loaded {} jewelries", allJewelries.size());
                allJewelries.forEach(j -> j.setImageUrl(jewelryShopProperties.getAddress() + j.getImageUrl()));
            } else {
                log.error("Could not load jewelries from jewelry shop service, url {}", address);
            }
        }
        return allJewelries;
    }
}
