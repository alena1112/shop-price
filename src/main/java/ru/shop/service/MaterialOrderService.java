package ru.shop.service;

import ru.shop.model.Shop;

import java.io.IOException;
import java.util.List;

public interface MaterialOrderService {
    void loadMaterials() throws IOException;
    void loadMaterials(Shop shop, String pageText);
    List<String> getOrders(Shop shop);
}
