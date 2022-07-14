package ru.shop.controllers.dto;

import lombok.*;
import ru.shop.model.Material;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MaterialDto {
    private String id;
    private String shop;
    private String name;
    private String price;
    private String unitPriceWithDelivery;
    private String number;
    private String imageURL;

    public static MaterialDto toMaterialDto(Material material) {
        return MaterialDto.builder()
                .id(String.valueOf(material.getId()))
                .shop(String.valueOf(material.getOrder().getShop().getId()))
                .imageURL(material.getImageURL())
                .name(material.getName())
                .number(String.valueOf(material.getNumber()))
                .price(String.valueOf(material.getPrice()))
                .unitPriceWithDelivery(String.valueOf(material.getUnitPriceWithDelivery()))
                .build();
    }
}
