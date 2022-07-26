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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class MaterialOrderServiceImpl implements MaterialOrderService {
    private static final Logger log = LoggerFactory.getLogger(MaterialOrderServiceImpl.class);

    @Autowired
    private MaterialService materialService;
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

    public MaterialOrderServiceImpl() {
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

    @Transactional
    @Override
    public void loadMaterials() throws IOException {
        List<MaterialOrder> materialOrders = new ArrayList<>();
        for (Shop shop : Shop.values()) {
            List<String> fileNames = loadFileNames(shop);
            if (!fileNames.isEmpty()) {
                for (String fileName : fileNames) {
                    boolean isExist = materialOrderDao.existsMaterialOrderByName(StringUtils.stripFilenameExtension(fileName));
                    if (!isExist) {
                        Optional<MaterialOrder> order = getShopReaderService(shop).parseFile(fileName, shop);
                        order.ifPresent(o -> {
                            calculatePriceForEachMaterial(o);
                            materialOrders.add(o);
                        });
                    } else {
                        log.warn("Material Order {} already loaded", fileName);
                    }
                }
            }
        }
        if (!materialOrders.isEmpty()) {
            materialOrderDao.saveAll(materialOrders);
            refreshAllCache();
            materialService.refreshMaterialCache();
            log.info("load material orders count: {}", materialOrders.size());
        }
    }

    @Transactional
    @Override
    public void loadMaterials(Shop shop, String pageText) {
        Optional<MaterialOrder> order = getShopReaderService(shop).parseText(pageText, shop);
        order.ifPresent(o -> {
            calculatePriceForEachMaterial(o);
            materialOrderDao.save(o);
            refreshCache(o.getShop());
            materialService.refreshMaterialCache();
            log.info("load material order page {}", o.getName());
        });
    }

    @Transactional(readOnly = true)
    @Override
    public List<String> getOrders(Shop shop) {
        List<String> orderNames = getOrdersFromCache(shop);
        log.info("load material order names {}", String.join("; ", orderNames));
        return orderNames;
    }

    private List<String> loadFileNames(Shop shop) throws IOException {
        List<String> fileNames = new ArrayList<>();
        //this file_names only for docker!
        InputStream inputStream = getClass().getResourceAsStream("/orders/" + shop.getId() + "/file_names.txt");
        if (inputStream != null) {
            try (InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                 BufferedReader reader = new BufferedReader(streamReader)) {
                List<String> found = reader.lines().collect(Collectors.toList());
                if (found.isEmpty()) {
                    log.warn("Could not find files for shop {}", shop.getId());
                } else {
                    fileNames = found;
                    log.info("Found files {}", String.join(", ", fileNames));
                }
            }
        } else {
            log.warn("Could not find files for shop {}", shop.getId());
        }
        return fileNames;
    }

    private HtmlShopReaderService getShopReaderService(Shop shop) {
        return switch (shop) {
            case GREEN_BIRD -> greenBirdParser;
            case PANDAHALL -> pandahallParser;
            case STILNAYA -> stilnayaParser;
            case LUXFURNITURA -> luxfurnituraParser;
            default -> (fileName, s) -> Optional.empty();
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
