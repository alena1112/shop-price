package ru.shop.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.shop.controllers.dto.JewelryMaterialDto;
import ru.shop.controllers.dto.JewelryMaterialsDto;
import ru.shop.controllers.dto.MaterialDto;
import ru.shop.dao.MaterialDao;
import ru.shop.model.Material;
import ru.shop.model.MaterialOrder;
import ru.shop.model.Shop;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MaterialServiceTest {
    @Mock
    private MaterialDao materialDao;
    @InjectMocks
    private MaterialServiceBean service;

    public static Stream<Arguments> getAllMaterialsParams() {
        return Stream.of(
                Arguments.of(Shop.STILNAYA, "lux_order", "lux_material", 0),
                Arguments.of(Shop.LUXFURNITURA, "lux_order1", "lux_material", 0),
                Arguments.of(Shop.GREEN_BIRD, "green_order", "lux_material", 0),
                Arguments.of(Shop.GREEN_BIRD, "green_order", "green_material2", 1),
                Arguments.of(Shop.GREEN_BIRD, "green_order", "green_mat", 2),
                Arguments.of(Shop.GREEN_BIRD, "green_order", "material2", 1)
        );
    }

    @AfterEach
    public void tearDown() {
        verifyNoMoreInteractions(materialDao);
    }

    @ParameterizedTest
    @MethodSource("getAllMaterialsParams")
    public void getAllMaterials(Shop shop, String materialOrderName, String materialName, int expected) {
        Material material1 = createMaterial(Shop.LUXFURNITURA, "lux_order", "lux_material");
        Material material2 = createMaterial(Shop.GREEN_BIRD, "green_order", "green_material");
        Material material3 = createMaterial(Shop.GREEN_BIRD, "green_order", "green_material2");

        when(materialDao.findAll()).thenReturn(List.of(material1, material2, material3));

        assertEquals(expected, service.getAllMaterials(shop, materialOrderName, materialName).size());
    }

    @Test
    public void getMaterialById() {
        Material material1 = createMaterial(10L, Shop.LUXFURNITURA, "lux_order", "lux_material");
        Material material2 = createMaterial(0L, Shop.LUXFURNITURA, "lux_order", "lux_material");

        when(materialDao.findAll()).thenReturn(List.of(material1, material2));

        Material actual = service.getMaterialById(0L).orElse(null);

        assertNotNull(actual);
        assertEquals(0L, actual.getId());
    }

    @Test
    public void calculatePrice() {
        Material material1 = createMaterial(10L, Shop.LUXFURNITURA, "lux_order", "lux_material", 100.0, 0.0, 10);
        Material material2 = createMaterial(0L, Shop.GREEN_BIRD, "green_order", "green_material", 50.0, 11.0, 2);
        Material material3 = createMaterial(3L, Shop.GREEN_BIRD, "green_order", "green_material2", 1000.0, 0.0, 1);

        JewelryMaterialsDto dto = new JewelryMaterialsDto(0L, List.of(
                new JewelryMaterialDto(10L, 1, "", ""),
                new JewelryMaterialDto(5L, 1, "", ""),
                new JewelryMaterialDto(3L, 10, "", ""),
                new JewelryMaterialDto(0L, 3, "", "")
        ));

        when(materialDao.findAll()).thenReturn(List.of(material1, material2, material3));
        double actial = service.calculatePrice(dto);

        //(100 + 0)/10*1 + (1000 + 0)/1*10 + (50 + 11)/2*3
        assertEquals(10101.5, actial);
    }

    @Test
    public void saveMaterial() {
        when(materialDao.findAll()).thenReturn(List.of(new Material()));
        service.saveMaterial(new MaterialDto());
        verify(materialDao).save(any(Material.class));
    }

    @Test
    public void refreshMaterialCacheTest() {
        when(materialDao.findAll()).thenReturn(List.of(new Material()));
        service.refreshMaterialCache();
    }

    private Material createMaterial(Shop shop, String materialOrderName, String materialName) {
        return createMaterial(1L, shop, materialOrderName, materialName, 0.0, 0.0, 1);
    }

    private Material createMaterial(Long id, Shop shop, String materialOrderName, String materialName) {
        return createMaterial(id, shop, materialOrderName, materialName, 0.0, 0.0, 1);
    }

    private Material createMaterial(Long id, Shop shop, String materialOrderName, String materialName, Double price,
                                    Double delivery, Integer number) {
        MaterialOrder materialOrder = new MaterialOrder();
        materialOrder.setName(materialOrderName);
        materialOrder.setShop(shop);
        materialOrder.setShop(shop);
        Material material = new Material();
        material.setId(id);
        material.setName(materialName);
        material.setOrder(materialOrder);
        material.setPrice(price);
        material.setDelivery(delivery);
        material.setNumber(number);
        return material;
    }
}
