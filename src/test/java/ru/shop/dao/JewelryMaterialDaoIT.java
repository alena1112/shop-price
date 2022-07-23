package ru.shop.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.shop.model.JewelryMaterial;
import ru.shop.model.Material;
import ru.shop.model.MaterialOrder;
import ru.shop.model.Shop;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JewelryMaterialDaoIT extends DatabaseIntegrationTest {
    @Autowired
    private JewelryMaterialDao dao;
    @Autowired
    private EntityManager manager;

    @Test
    public void jewelryMaterialTest() {
        MaterialOrder materialOrder = new MaterialOrder();
        materialOrder.setName("order");
        materialOrder.setShop(Shop.LUXFURNITURA);
        materialOrder = manager.merge(materialOrder);

        Material material = new Material();
        material.setOrder(materialOrder);
        material = manager.merge(material);

        final long jewelryId = 10L;

        dao.saveJewelryMaterial(jewelryId, material.getId(), 2);

        List<JewelryMaterial> foundJewelry = dao.getMaterialsByJewelryId(jewelryId);
        assertEquals(1, foundJewelry.size());
        assertEquals(2, foundJewelry.get(0).getNumber());

        dao.deleteAllByJewelry(jewelryId);

        foundJewelry = dao.getMaterialsByJewelryId(jewelryId);
        assertEquals(0, foundJewelry.size());
    }
}
