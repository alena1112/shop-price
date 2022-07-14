package ru.shop.controllers.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class JewelryMaterialsDto {
    private List<JewelryMaterialDto> materials;
}
