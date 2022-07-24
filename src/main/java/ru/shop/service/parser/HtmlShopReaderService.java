package ru.shop.service.parser;

import ru.shop.model.MaterialOrder;
import ru.shop.model.Shop;

import java.util.Optional;

public interface HtmlShopReaderService {
    Optional<MaterialOrder> parseFile(String fileName, Shop shop);
    default Optional<MaterialOrder> parseText(String text, Shop shop) {
        return Optional.empty();
    }
}
