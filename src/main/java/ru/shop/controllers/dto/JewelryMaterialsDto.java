package ru.shop.controllers.dto;

import lombok.*;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class JewelryMaterialsDto {
    private final Long jewelryId;
    private final List<JewelryMaterialDto> materials;
}
