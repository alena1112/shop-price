package ru.shop.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JewelryDto {
    private final Long id;
    private final String name;
    private final String description;
    private final Double price;
    private String imageUrl;

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
