package ru.shop.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shop.controllers.dto.JewelryMaterialDto;
import ru.shop.controllers.dto.JewelryMaterialsDto;
import ru.shop.controllers.dto.MaterialDto;
import ru.shop.dao.MaterialDao;
import ru.shop.model.Material;
import ru.shop.model.Shop;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MaterialServiceBean implements MaterialService {
    private static final Logger log = LoggerFactory.getLogger(MaterialServiceBean.class);

    private final MaterialDao materialDao;

    private List<Material> allMaterials = new ArrayList<>();

    @Override
    public List<Material> getAllMaterials(boolean isReload, Shop shop, String materialOrderName, String materialName) {
        if (isReload) {
            allMaterials = materialDao.findAll();
            log.info("load all materials, count: {}", allMaterials.size());
        }

        List<Material> filteredMaterials = allMaterials.stream()
                .filter(m -> shop == null || m.getOrder().getShop() == shop)
                .filter(m -> materialOrderName == null || m.getOrder().getName().equals(materialOrderName))
                .filter(m -> materialName == null || m.getName().toLowerCase().contains(materialName.toLowerCase()))
                .collect(Collectors.toList());

        log.info("filter materials: shop {}, materialOrderName {}, materialName {}, count: {}",
                shop != null ? shop.getId() : "", materialOrderName, materialName, filteredMaterials.size());
        return filteredMaterials;
    }

    @Override
    public Optional<Material> getMaterialById(Long id) {
        return getAllMaterials().stream().filter(m -> m.getId().equals(id)).findFirst();
    }

    @Override
    public double calculatePrice(JewelryMaterialsDto jewelryMaterialsDto) {
        double price = 0;
        for (JewelryMaterialDto dto : jewelryMaterialsDto.getMaterials()) {
            Optional<Material> material = getAllMaterials().stream().filter(m ->
                    m.getId().equals(Long.parseLong(dto.getMaterialId()))).findFirst();
            if (material.isPresent()) {
                price += material.get().getUnitPriceWithDelivery() * Integer.parseInt(dto.getCount());
            }
        }
        log.info("Calculated original price - {}", price);
        return price;
    }

    @Override
    public void saveMaterial(MaterialDto materialDto) {
        Material material = materialDto.getId() != null ? getMaterialById(Long.parseLong(materialDto.getId())).orElse(null) : null;
        if (material == null) {
            material = new Material();
            material.setImageURL(materialDto.getImageURL());
            material.setName(materialDto.getName());
            material.setPrice(Double.parseDouble(materialDto.getPrice()));
            material.setDelivery(0.0);
        }
        material.setNumber(Integer.parseInt(materialDto.getNumber()));
        materialDao.save(material);
        log.info("save material, id: {}, {}", material.getId(), material.getName());
    }

    public List<Material> getAllMaterials() {
        if (allMaterials.isEmpty()) {
            allMaterials = materialDao.findAll();
            log.info("load all materials, count: {}", allMaterials.size());
        }
        return allMaterials;
    }
}
