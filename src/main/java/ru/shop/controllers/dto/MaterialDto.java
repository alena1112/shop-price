package ru.shop.controllers.dto;

import lombok.*;
import ru.shop.model.Material;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MaterialDto {
    private Long id;
    private String shop;
    private String name;
    private Double price;
    private Double unitPriceWithDelivery;
    private Integer number;
    private String imageURL;

    public static MaterialDto toMaterialDto(Material material) {
        return MaterialDto.builder()
                .id(material.getId())
                .shop(String.valueOf(material.getOrder().getShop().getId()))
                .imageURL(material.getImageURL())
                .name(material.getName())
                .number(material.getNumber())
                .price(material.getPrice())
                .unitPriceWithDelivery(material.getUnitPriceWithDelivery())
                .build();
    }
}
