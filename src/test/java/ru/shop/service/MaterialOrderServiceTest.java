package ru.shop.service;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.shop.dao.MaterialOrderDao;
import ru.shop.model.Material;
import ru.shop.model.MaterialOrder;
import ru.shop.model.Shop;
import ru.shop.service.parser.HtmlShopReaderService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MaterialOrderServiceTest {
    @Mock
    private MaterialService materialService;
    @Mock
    private MaterialOrderDao materialOrderDao;
    @Mock
    private HtmlShopReaderService greenBirdParser;
    @Mock
    private HtmlShopReaderService pandahallParser;
    @Mock
    private HtmlShopReaderService stilnayaParser;
    @Mock
    private HtmlShopReaderService luxfurnituraParser;
    @InjectMocks
    private MaterialOrderServiceBean service;

    @Captor
    private ArgumentCaptor<List<MaterialOrder>> materialOrdersCaptor;
    @Captor
    private ArgumentCaptor<MaterialOrder> materialOrderCaptor;

    @AfterEach
    public void tearDown() {
        verifyNoMoreInteractions(materialOrderDao, greenBirdParser, pandahallParser, stilnayaParser, luxfurnituraParser);
    }

    @Test
    @SneakyThrows
    public void loadMaterialsTest() throws Exception {
        MaterialOrder luxfurnituraMO = createMaterialOrder("luxfurnitura_14.06.2022", Shop.LUXFURNITURA);
        MaterialOrder greenBirdMO = createMaterialOrder("greenBird_09.04.2019", Shop.GREEN_BIRD);
        MaterialOrder pandahallMO = createMaterialOrder("pandahall_23.02.2019.html", Shop.PANDAHALL);
        MaterialOrder stilnayaMO = createMaterialOrder("stilnaya_16.04.2019.html", Shop.STILNAYA);

        when(materialOrderDao.existsMaterialOrderByName(any(String.class))).thenReturn(false);
        when(greenBirdParser.parseFile(any(String.class), eq(Shop.GREEN_BIRD))).thenReturn(Optional.of(greenBirdMO));
        when(pandahallParser.parseFile(any(String.class), eq(Shop.PANDAHALL))).thenReturn(Optional.of(pandahallMO));
        when(stilnayaParser.parseFile(any(String.class), eq(Shop.STILNAYA))).thenReturn(Optional.of(stilnayaMO));
        when(luxfurnituraParser.parseFile(any(String.class), eq(Shop.LUXFURNITURA))).thenReturn(Optional.of(luxfurnituraMO));
        for (Shop s : Shop.values()) {
            when(materialOrderDao.getAllOrderNames(s)).thenReturn(new ArrayList<>());
        }

        service.loadMaterials();

        verify(materialService).refreshMaterialCache();
        verify(materialOrderDao).saveAll(materialOrdersCaptor.capture());
        List<MaterialOrder> value = materialOrdersCaptor.getValue();
        assertFalse(value.isEmpty());
        assertEquals(250, value.get(0).getMaterials().get(0).getDelivery());
    }

    @Test
    public void loadMaterialsTestViaText() {
        final String pageText = "test";
        final Shop shop = Shop.LUXFURNITURA;
        final MaterialOrder luxfurnituraMO = createMaterialOrder("luxfurnitura", shop);

        when(luxfurnituraParser.parseText(pageText, shop)).thenReturn(Optional.of(luxfurnituraMO));
        when(materialOrderDao.getAllOrderNames(shop)).thenReturn(new ArrayList<>());

        service.loadMaterials(shop, pageText);

        verify(materialService).refreshMaterialCache();
        verify(materialOrderDao).save(materialOrderCaptor.capture());
        MaterialOrder value = materialOrderCaptor.getValue();
        assertNotNull(value);
        assertEquals(250, value.getMaterials().get(0).getDelivery());
    }

    @Test
    public void getOrdersTestWithNullShop() {
        for (Shop s : Shop.values()) {
            when(materialOrderDao.getAllOrderNames(s)).thenReturn(List.of(s.name()));
        }

        List<String> actualResult = service.getOrders(null);
        assertEquals(Shop.values().length, actualResult.size());
    }

    @Test
    public void getOrdersTest() {
        final Shop shop = Shop.LUXFURNITURA;
        when(materialOrderDao.getAllOrderNames(shop)).thenReturn(List.of(shop.name()));

        List<String> actualResult = service.getOrders(shop);
        assertEquals(1, actualResult.size());
    }

    private MaterialOrder createMaterialOrder(String orderName, Shop shop) {
        MaterialOrder materialOrder = new MaterialOrder();
        materialOrder.setName(orderName);
        materialOrder.setShop(shop);
        materialOrder.setDeliveryPrice(500);
        Material material1 = new Material();
        Material material2 = new Material();
        materialOrder.setMaterials(List.of(material1, material2));
        return materialOrder;
    }
}
