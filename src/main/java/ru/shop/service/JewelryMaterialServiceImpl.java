package ru.shop.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shop.dao.JewelryMaterialDao;
import ru.shop.model.JewelryMaterial;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JewelryMaterialServiceImpl implements JewelryMaterialService {
    private static final Logger log = LoggerFactory.getLogger(JewelryMaterialServiceImpl.class);

    private final JewelryMaterialDao jewelryMaterialDao;

    @Transactional
    @Override
    public void saveJewelryMaterials(Long jewelryId, List<JewelryMaterial> jewelryMaterials) {
        jewelryMaterialDao.deleteAllByJewelryMaterialId_Jewelry(jewelryId);
        jewelryMaterialDao.saveAll(jewelryMaterials);
        log.info("Saved all jewelry materials, jewelry id {}, materials count {}", jewelryId, jewelryMaterials.size());
    }

    @Transactional(readOnly = true)
    @Override
    public List<JewelryMaterial> getMaterialsByJewelryId(Long jewelryId) {
        List<JewelryMaterial> materials = jewelryMaterialDao.getMaterialsByJewelryId(jewelryId);
        log.info("found {} materials for jewelry id {}", materials.size(), jewelryId);
        return materials;
    }
}
