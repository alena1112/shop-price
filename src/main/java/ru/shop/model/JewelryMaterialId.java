package ru.shop.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
public class JewelryMaterialId implements Serializable {
    private Long jewelry;
    private Long materialId;
}
