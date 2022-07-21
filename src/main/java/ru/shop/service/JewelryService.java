package ru.shop.service;

import ru.shop.controllers.dto.JewelryDto;

import java.util.List;

public interface JewelryService {
    List<JewelryDto> getAllJewelries();
}
