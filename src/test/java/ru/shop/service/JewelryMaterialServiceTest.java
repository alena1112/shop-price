package ru.shop.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.shop.dao.JewelryMaterialDao;
import ru.shop.model.JewelryMaterial;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JewelryMaterialServiceTest {
    @Mock
    private JewelryMaterialDao jewelryMaterialDao;
    @InjectMocks
    private JewelryMaterialServiceBean service;

    @AfterEach
    public void tearDown() {
        verifyNoMoreInteractions(jewelryMaterialDao);
    }

    @Test
    public void saveJewelryMaterialsTest() {
        service.saveJewelryMaterials(1L, List.of(new JewelryMaterial(1L, 1L, null, 1)));

        verify(jewelryMaterialDao).deleteAllByJewelry(1L);
        verify(jewelryMaterialDao).saveJewelryMaterial(1L, 1L, 1);
    }

    @Test
    public void getMaterialsByJewelryIdTest() {
        when(jewelryMaterialDao.getMaterialsByJewelryId(1L)).thenReturn(List.of(new JewelryMaterial()));
        assertNotNull(service.getMaterialsByJewelryId(1L));
    }
}
