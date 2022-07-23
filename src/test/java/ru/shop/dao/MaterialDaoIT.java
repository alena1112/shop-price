package ru.shop.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.shop.model.Material;
import ru.shop.model.MaterialOrder;
import ru.shop.model.Shop;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MaterialDaoIT extends DatabaseIntegrationTest {
    @Autowired
    private MaterialDao dao;
    @Autowired
    private EntityManager manager;

    @Test
    public void testMaterial() {
        MaterialOrder materialOrder = new MaterialOrder();
        materialOrder.setName("order");
        materialOrder.setShop(Shop.LUXFURNITURA);
        materialOrder = manager.merge(materialOrder);

        Material material = new Material();
        material.setOrder(materialOrder);

        dao.save(material);

        List<Material> materials = dao.findAll();
        assertFalse(materials.isEmpty());
        material = materials.get(0);
        assertEquals(1, materials.size());

        dao.delete(material);

        materials = dao.findAll();
        assertTrue(materials.isEmpty());
    }
}
