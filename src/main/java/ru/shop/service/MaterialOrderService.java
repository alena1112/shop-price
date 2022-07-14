package ru.shop.service;

import ru.shop.model.Shop;

import java.util.List;

public interface MaterialOrderService {
    void loadMaterials();
    void loadMaterials(Shop shop, String pageText);
    List<String> getOrders(Shop shop);
}
