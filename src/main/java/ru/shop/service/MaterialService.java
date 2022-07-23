package ru.shop.service;

import ru.shop.controllers.dto.JewelryMaterialsDto;
import ru.shop.controllers.dto.MaterialDto;
import ru.shop.model.Material;
import ru.shop.model.Shop;

import java.util.List;
import java.util.Optional;

public interface MaterialService {
    List<Material> getAllMaterials(Shop shop, String materialOrderName, String materialName);
    Optional<Material> getMaterialById(Long id);
    double calculatePrice(JewelryMaterialsDto jewelryMaterialsDto);
    void saveMaterial(MaterialDto materialDto);
    void refreshMaterialCache();
}
