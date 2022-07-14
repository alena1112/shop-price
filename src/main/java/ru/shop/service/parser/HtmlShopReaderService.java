package ru.shop.service.parser;

import ru.shop.model.MaterialOrder;
import ru.shop.model.Shop;

import java.io.File;
import java.util.Optional;

public interface HtmlShopReaderService {
    Optional<MaterialOrder> parse(File file, Shop shop);
    default Optional<MaterialOrder> parse(String text, Shop shop) {
        return Optional.empty();
    }
}
