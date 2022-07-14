package ru.shop.controllers.dto;

import lombok.*;
import ru.shop.model.Shop;

@Getter
@RequiredArgsConstructor
public class PageDto {
    private final Shop shop;
    private final String text;
}
