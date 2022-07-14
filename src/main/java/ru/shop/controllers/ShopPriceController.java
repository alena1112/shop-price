package ru.shop.controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ru.shop.JewelryShopProperties;
import ru.shop.controllers.dto.JewelryDto;
import ru.shop.controllers.dto.MaterialDto;
import ru.shop.controllers.dto.ShopDto;
import ru.shop.model.Shop;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ShopPriceController {
    private static final Logger log = LoggerFactory.getLogger(ShopPriceController.class);

    private final RestTemplate restTemplate;
    private final JewelryShopProperties jewelryShopProperties;

    private List<JewelryDto> allJewelries;//TODO сделать норм кэш

    @GetMapping(value = "/shop", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ShopDto> getAllShops() {
        log.info("request to get all shops");
        return Arrays.stream(Shop.values()).map(s -> new ShopDto(s.name(), s.getId())).collect(Collectors.toList());
    }

    @GetMapping(value = "/jewelry/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<JewelryDto> loadJewelries() {
        log.info("request to get all jewelries");
        return getAllJewelries();
    }

    @GetMapping(value = "/jewelry/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public JewelryDto getJewelry(@PathVariable("id") Long jewelryId) {
        log.info("request to get a jewelry by id {}", jewelryId);
        return getAllJewelries().stream().filter(j -> j.getId().equals(jewelryId)).findFirst().orElse(null);
    }

    private List<JewelryDto> getAllJewelries() {
        if (allJewelries == null) {
            log.info("start loading jewelries from jewelry shop service");
            String address = jewelryShopProperties.getAddress() + jewelryShopProperties.getAllJewelryUrl();
            ResponseEntity<List<JewelryDto>> responseEntity =
                    restTemplate.exchange(
                            address,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {
                            }
                    );
            allJewelries = responseEntity.getBody();
            if (allJewelries != null) {
                log.info("loaded {} jewelries", allJewelries.size());
                allJewelries.forEach(j -> j.setImageUrl(jewelryShopProperties.getAddress() + j.getImageUrl()));
            } else {
                log.error("could not load jewelries from jewelry shop service, url {}", address);
            }
        }
        return allJewelries;
    }
}
