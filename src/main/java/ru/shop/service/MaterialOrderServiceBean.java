package ru.shop.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shop.dao.MaterialOrderDao;
import ru.shop.model.Material;
import ru.shop.model.MaterialOrder;
import ru.shop.model.Shop;
import ru.shop.service.parser.HtmlShopReaderService;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MaterialOrderServiceBean implements MaterialOrderService {
    private static final Logger log = LoggerFactory.getLogger(MaterialOrderServiceBean.class);

    private final MaterialOrderDao materialOrderDao;
    private final HtmlShopReaderService greenBirdParser;
    private final HtmlShopReaderService pandahallParser;
    private final HtmlShopReaderService stilnayaParser;
    private final HtmlShopReaderService luxfurnituraParser;

    @Override
    public void loadMaterials() {
        List<MaterialOrder> materialOrders = new ArrayList<>();
        Arrays.stream(Shop.values()).forEach(shop -> {
            try {
                URL resource = MaterialServiceBean.class.getResource("/orders/" + shop.getId());
                if (resource != null) {
                    File[] files = new File(resource.toURI()).listFiles();
                    if (files != null) {
                        Arrays.stream(files).forEach(file -> {
                            boolean isExist = materialOrderDao.existsMaterialOrderByName(file.getName());
                            if (!isExist) {
                                Optional<MaterialOrder> order = getShopReaderService(shop).parse(file, shop);
                                order.ifPresent(o -> {
                                    calculatePriceForEachMaterial(o);
                                    materialOrders.add(o);
                                });
                            } else {
                                log.warn("Material Order {} already loaded", file.getName());
                            }
                        });
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        });
        materialOrderDao.saveAll(materialOrders);
        log.info("load material orders count: {}", materialOrders.size());
    }

    @Override
    public void loadMaterials(Shop shop, String pageText) {
        Optional<MaterialOrder> order = getShopReaderService(shop).parse(pageText, shop);
        order.ifPresent(o -> {
            calculatePriceForEachMaterial(o);
            materialOrderDao.save(o);
            log.info("load material order page {}", o.getName());
        });
    }

    @Override
    public List<String> getOrders(Shop shop) {
        List<String> orderNames = materialOrderDao.getAllOrderNames(shop);//TODO закешировать
        log.info("load material order names {}", String.join("; ", orderNames));
        return orderNames;
    }

    private HtmlShopReaderService getShopReaderService(Shop shop) {
        return switch (shop) {
            case GREEN_BIRD -> greenBirdParser;
            case PANDAHALL -> pandahallParser;
            case STILNAYA -> stilnayaParser;
            case LUXFURNITURA -> luxfurnituraParser;
            default -> (f, s) -> Optional.empty();
        };
    }

    private static void calculatePriceForEachMaterial(MaterialOrder order) {
        int numOfItems = order.getMaterials().size();
        double delivery = order.getDeliveryPrice() / numOfItems;
        for (Material item : order.getMaterials()) {
            item.setDelivery(delivery);
        }
    }
}
