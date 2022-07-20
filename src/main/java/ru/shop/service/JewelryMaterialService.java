package ru.shop.service;

import ru.shop.model.JewelryMaterial;

import java.util.List;

public interface JewelryMaterialService {
    void saveJewelryMaterials(Long jewelryId, List<JewelryMaterial> jewelryMaterials);
    List<JewelryMaterial> getMaterialsByJewelryId(Long jewelryId);
}
