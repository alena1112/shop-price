package ru.shop.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.shop.controllers.dto.ShopDto;
import ru.shop.model.Shop;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ShopController {
    private static final Logger log = LoggerFactory.getLogger(ShopController.class);

    @GetMapping(value = "/shop", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ShopDto> getAllShops() {
        log.info("request to get all shops");
        return Arrays.stream(Shop.values()).map(s -> new ShopDto(s.name(), s.getId())).collect(Collectors.toList());
    }
}
