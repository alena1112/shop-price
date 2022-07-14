package ru.shop.controllers.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JewelryMaterialDto {
    private final String materialId;
    private final String count;
}
