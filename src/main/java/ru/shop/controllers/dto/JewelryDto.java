package ru.shop.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class JewelryDto {
    private final Long id;
    private final String name;
    private final String description;
    private final Double price;
    private String imageUrl;
    private List<JewelryMaterialDto> materials;

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setMaterials(List<JewelryMaterialDto> materials) {
        this.materials = materials;
    }
}
