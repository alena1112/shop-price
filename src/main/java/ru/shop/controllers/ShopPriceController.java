package ru.shop.controllers;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ShopPriceController {
    private static final Logger log = LoggerFactory.getLogger(ShopPriceController.class);

    @GetMapping(value = "/shop", produces = MediaType.APPLICATION_JSON_VALUE)
    public RestResponse<List<ShopDto>> getAllShops() {
        log.info("request to get all shops");
        return RestResponse.withData(
                Arrays.stream(Shop.values()).map(s -> new ShopDto(s.name(), s.getId())).collect(Collectors.toList()),
                "Shops loaded successfully"
        );
    }
}
