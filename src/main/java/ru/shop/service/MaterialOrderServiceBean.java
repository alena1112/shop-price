package ru.shop.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
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
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class MaterialOrderServiceBean implements MaterialOrderService {
    private static final Logger log = LoggerFactory.getLogger(MaterialOrderServiceBean.class);

    @Autowired
    private MaterialOrderDao materialOrderDao;
    @Autowired
    private HtmlShopReaderService greenBirdParser;
    @Autowired
    private HtmlShopReaderService pandahallParser;
    @Autowired
    private HtmlShopReaderService stilnayaParser;
    @Autowired
    private HtmlShopReaderService luxfurnituraParser;

    private final LoadingCache<Shop, List<String>> MATERIAL_ORDERS_CACHE;

    public MaterialOrderServiceBean() {
        MATERIAL_ORDERS_CACHE = CacheBuilder
                .newBuilder()
                .expireAfterWrite(60, TimeUnit.SECONDS)
                .maximumSize(10000)
                .build(new CacheLoader<>() {
                    @Override
                    public List<String> load(Shop key) {
                        List<String> orderNames = materialOrderDao.getAllOrderNames(key);
                        log.info("load material order names {} to cache", String.join("; ", orderNames));
                        return orderNames;
                    }
                });
    }

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
                            boolean isExist = materialOrderDao.existsMaterialOrderByName(StringUtils.stripFilenameExtension(file.getName()));
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
        refreshAllCache();
        log.info("load material orders count: {}", materialOrders.size());
    }

    @Override
    public void loadMaterials(Shop shop, String pageText) {
        Optional<MaterialOrder> order = getShopReaderService(shop).parse(pageText, shop);
        order.ifPresent(o -> {
            calculatePriceForEachMaterial(o);
            materialOrderDao.save(o);
            refreshCache(o.getShop());
            log.info("load material order page {}", o.getName());
        });
    }

    @Override
    public List<String> getOrders(Shop shop) {
        List<String> orderNames = getOrdersFromCache(shop);
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
            item.setDelivery(PriceHelper.round(delivery));
        }
    }

    private List<String> getOrdersFromCache(Shop shop) {
        if (shop == null) {
            List<String> result = new ArrayList<>();
            for (Shop s : Shop.values()) {
                result.addAll(MATERIAL_ORDERS_CACHE.getUnchecked(s));
            }
            return result;
        }
        return MATERIAL_ORDERS_CACHE.getUnchecked(shop);
    }

    private void refreshCache(Shop shop) {
        MATERIAL_ORDERS_CACHE.invalidate(shop);
        MATERIAL_ORDERS_CACHE.refresh(shop);
    }

    private void refreshAllCache() {
        Arrays.stream(Shop.values()).forEach(MATERIAL_ORDERS_CACHE::refresh);
    }
}
