package ru.shop.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.shop.model.MaterialOrder;
import ru.shop.model.Shop;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

public class MaterialOrderDaoIT extends DatabaseIntegrationTest {
    @Autowired
    private MaterialOrderDao dao;
    @Autowired
    private EntityManager manager;

    @Test
    public void existsMaterialOrderByNameTest() {
        String order = "order";

        assertFalse(dao.existsMaterialOrderByName(order));

        MaterialOrder materialOrder = new MaterialOrder();
        materialOrder.setName(order);
        materialOrder.setShop(Shop.STILNAYA);
        manager.persist(materialOrder);

        assertTrue(dao.existsMaterialOrderByName(order));
    }

    @Test
    public void getAllOrderNamesTest() {
        final Shop shop = Shop.GREEN_BIRD;
        assertEquals(0, dao.getAllOrderNames(shop).size());

        MaterialOrder materialOrder = new MaterialOrder();
        materialOrder.setName("order");
        materialOrder.setShop(shop);
        manager.persist(materialOrder);

        assertEquals(1, dao.getAllOrderNames(shop).size());
    }
}
