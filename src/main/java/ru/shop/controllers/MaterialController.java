package ru.shop.controllers;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.shop.SPException;
import ru.shop.controllers.dto.JewelryMaterialsDto;
import ru.shop.controllers.dto.MaterialDto;
import ru.shop.controllers.dto.PageDto;
import ru.shop.model.Shop;
import ru.shop.service.MaterialOrderService;
import ru.shop.service.MaterialService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(
        value = {"/material"},
        produces = {"application/json"}
)
@RequiredArgsConstructor
public class MaterialController {
    private static final Logger log = LoggerFactory.getLogger(MaterialController.class);

    private final MaterialService materialService;
    private final MaterialOrderService materialOrderService;

    @GetMapping
    public RestResponse<List<MaterialDto>> getAllMaterials(@RequestParam(value = "shop", required = false) Shop shop,
                                                           @RequestParam(value = "order", required = false) String order,
                                                           @RequestParam(value = "material", required = false) String materialName) {
        log.info("request to get all materials, shop {}, order {}, material {}", shop, order, materialName);
        return RestResponse.withData(
                materialService.getAllMaterials(
                                shop == null,//TODO удалить параметр и сделать нормальный кеш
                                shop,
                                StringUtils.defaultIfBlank(order, null),
                                StringUtils.defaultIfBlank(materialName, null))
                        .stream()
                        .map(MaterialDto::toMaterialDto)
                        .collect(Collectors.toList()));
    }

    @PostMapping
    public RestResponse<?> saveMaterial(@RequestBody MaterialDto materialDto) {
        log.info("request to save material");
        materialService.saveMaterial(materialDto);
        return RestResponse.ok();
    }

    @GetMapping("/{id}")
    public RestResponse<MaterialDto> getMaterial(@PathVariable("id") String materialId) {
        log.info("request to get a material by id {}", materialId);
        return RestResponse.withData(
                materialService.getMaterialById(Long.parseLong(materialId)).map(MaterialDto::toMaterialDto).orElse(null));
    }

    @PostMapping(value = "/load")
    public RestResponse<?> loadMaterials() {
        log.info("request to start loading materials");
        materialOrderService.loadMaterials();
        return RestResponse.ok();
    }

    @PostMapping(value = "/loadPage")
    public RestResponse<?> loadPageMaterials(@RequestBody PageDto pageDto) {
        log.info("request to start loading page materials");
        SPException.checkNotNull(pageDto.getShop(), "Shop is null!");
        SPException.checkNotEmpty(pageDto.getText(), "Page text is null!");
        materialOrderService.loadMaterials(pageDto.getShop(), pageDto.getText());
        return RestResponse.ok();
    }

    @GetMapping("/orders")
    public RestResponse<List<String>> getOrders(@RequestParam(value = "shop", required = false) Shop shop) {
        log.info("request to get all material orders");
        return RestResponse.withData(materialOrderService.getOrders(shop));
    }

    @PostMapping(value = "/calculate")
    public RestResponse<Double> calculatePrice(@RequestBody JewelryMaterialsDto jewelryMaterialsDto) {
        log.info("request to start calculating price");
        return RestResponse.withData(materialService.calculatePrice(jewelryMaterialsDto));
    }
}
