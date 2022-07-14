package ru.shop.controllers;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
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
    public List<MaterialDto> getAllMaterials(@RequestParam(value = "shop", required = false) Shop shop,
                                             @RequestParam(value = "order", required = false) String order,
                                             @RequestParam(value = "material", required = false) String materialName) {
        log.info("request to get all materials, shop {}, order {}, material {}", shop, order, materialName);
        return materialService.getAllMaterials(
                        shop == null,//TODO удалить параметр и сделать нормальный кеш
                        shop,
                        StringUtils.defaultIfBlank(order, null),
                        StringUtils.defaultIfBlank(materialName, null))
                .stream()
                .map(MaterialDto::toMaterialDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public String saveMaterial(@RequestBody MaterialDto materialDto) {
        log.info("request to save material");
        materialService.saveMaterial(materialDto);
        return "OK";
    }

    @GetMapping("/{id}")
    public MaterialDto getMaterial(@PathVariable("id") String materialId) {
        log.info("request to get a material by id {}", materialId);
        return materialService.getMaterialById(Long.parseLong(materialId)).map(MaterialDto::toMaterialDto).orElse(null);
    }

    @PostMapping(value = "/load")
    public String loadMaterials() {
        log.info("request to start loading materials");
        materialOrderService.loadMaterials();
        return "OK";
    }

    @PostMapping(value = "/loadPage")
    public String loadPageMaterials(@RequestBody PageDto pageDto) {
        log.info("request to start loading page materials");
        checkNotNull(pageDto.getShop());
        checkNotEmpty(pageDto.getText());
        materialOrderService.loadMaterials(pageDto.getShop(), pageDto.getText());
        return "OK";
    }

    @GetMapping("/orders")
    public List<String> getOrders(@RequestParam(value = "shop", required = false) Shop shop) {
        log.info("request to get all material orders");
        return materialOrderService.getOrders(shop);
    }

    @PostMapping(value = "/calculate")
    public Double calculatePrice(@RequestBody JewelryMaterialsDto jewelryMaterialsDto) {
        log.info("request to start calculating price");
        return materialService.calculatePrice(jewelryMaterialsDto);
    }

    private static void checkNotEmpty(String str) {//TODO create exception class and handler
        if (str == null || str.isEmpty()) {
            throw new NullPointerException();
        }
    }

    private static void checkNotNull(Object obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
    }
}
