package ru.shop.controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.shop.SPException;
import ru.shop.controllers.dto.JewelryDto;
import ru.shop.controllers.dto.JewelryMaterialDto;
import ru.shop.controllers.dto.JewelryMaterialsDto;
import ru.shop.model.JewelryMaterial;
import ru.shop.model.JewelryMaterialId;
import ru.shop.model.Material;
import ru.shop.service.JewelryMaterialService;
import ru.shop.service.JewelryService;

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

    private final JewelryMaterialService jewelryMaterialService;
    private final JewelryService jewelryService;

    @GetMapping
    public RestResponse<List<JewelryDto>> loadJewelries() {
        log.info("request to get all jewelries");
        return RestResponse.withData(jewelryService.getAllJewelries(), "Jewelries loaded successfully");
    }

    @GetMapping(value = "/{id}")
    public RestResponse<JewelryDto> getJewelry(@PathVariable("id") Long jewelryId) {
        log.info("request to get a jewelry by id {}", jewelryId);
        JewelryDto jewelryDto = jewelryService.getAllJewelries().stream().filter(j -> j.getId().equals(jewelryId)).findFirst().orElse(null);
        if (jewelryDto != null) {
            jewelryDto.setMaterials(jewelryMaterialService.getMaterialsByJewelryId(jewelryId)
                    .stream().map(m -> {
                        Material material = m.getJewelryMaterialId().getMaterial();
                        return new JewelryMaterialDto(material.getId(), m.getNumber(),
                                material.getImageURL(), material.getName());
                    }).collect(Collectors.toList()));
        }
        return RestResponse.withData(jewelryDto, "Jewelry with materials loaded successfully");
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
                            new JewelryMaterialId(jewelryMaterialsDto.getJewelryId(), new Material(dto.getId())),
                            dto.getCount());
                })
                .collect(Collectors.toList());
        jewelryMaterialService.saveJewelryMaterials(jewelryMaterialsDto.getJewelryId(), result);
        return RestResponse.ok(String.format("Jewelry %s loaded successfully", jewelryMaterialsDto.getJewelryId()));
    }
}
