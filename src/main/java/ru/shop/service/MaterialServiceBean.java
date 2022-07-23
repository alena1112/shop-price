package ru.shop.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional
public class MaterialServiceBean implements MaterialService {
    private static final Logger log = LoggerFactory.getLogger(MaterialServiceBean.class);

    private final MaterialDao materialDao;

    private final LoadingCache<String, List<Material>> MATERIALS_CACHE;

    public MaterialServiceBean(MaterialDao materialDao) {
        this.materialDao = materialDao;

        MATERIALS_CACHE = CacheBuilder
                .newBuilder()
                .expireAfterWrite(60, TimeUnit.SECONDS)
                .maximumSize(10000)
                .build(new CacheLoader<>() {
                    @Override
                    public List<Material> load(String key) {
                        List<Material> allMaterials = materialDao.findAll();
                        log.info("load all materials to cache, count: {}", allMaterials.size());
                        return allMaterials;
                    }
                });
    }

    @Override
    public List<Material> getAllMaterials(Shop shop, String materialOrderName, String materialName) {
        List<Material> filteredMaterials = getMaterialsFromCache().stream()
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
        return getMaterialsFromCache().stream().filter(m -> m.getId().equals(id)).findFirst();
    }

    @Override
    public double calculatePrice(JewelryMaterialsDto jewelryMaterialsDto) {
        double price = 0;
        for (JewelryMaterialDto dto : jewelryMaterialsDto.getMaterials()) {
            Optional<Material> material = getMaterialsFromCache().stream().filter(m ->
                    m.getId().equals(dto.getId())).findFirst();
            if (material.isPresent()) {
                price += material.get().getUnitPriceWithDelivery() * dto.getCount();
            }
        }
        price = PriceHelper.round(price);
        log.info("Calculated original price - {}", price);
        return price;
    }

    @Override
    public void saveMaterial(MaterialDto materialDto) {
        Material material = materialDto.getId() != null ? getMaterialById(materialDto.getId()).orElse(null) : null;
        if (material == null) {
            material = new Material();
            material.setImageURL(materialDto.getImageURL());
            material.setName(materialDto.getName());
            material.setPrice(materialDto.getPrice());
            material.setDelivery(0.0);
        }
        material.setNumber(materialDto.getNumber());
        materialDao.save(material);
        refreshCache();
        log.info("save material, id: {}, {}", material.getId(), material.getName());
    }

    @Override
    public void refreshMaterialCache() {
        refreshCache();
    }

    private List<Material> getMaterialsFromCache() {
        return MATERIALS_CACHE.getUnchecked("ANY");
    }

    private void refreshCache() {
        MATERIALS_CACHE.refresh("ANY");
    }
}
