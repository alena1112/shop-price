package ru.shop.controllers.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JewelryMaterialDto {
    private Long id;
    private Integer count;
    private String imageURL;
    private String name;

    public JewelryMaterialDto(Long id, Integer count, String imageURL, String name) {
        this.id = id;
        this.count = count;
        this.imageURL = imageURL;
        this.name = name;
    }
}
