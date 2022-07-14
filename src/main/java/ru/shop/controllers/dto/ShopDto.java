package ru.shop.controllers.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ShopDto {
    private final String value;
    private final String name;
}
