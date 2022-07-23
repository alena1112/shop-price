package ru.shop.service;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.shop.model.MaterialOrder;
import ru.shop.model.Shop;
import ru.shop.service.parser.HtmlShopReaderService;
import ru.shop.service.parser.HtmlShopReaderServiceBean;

import java.io.File;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class HtmlShopReaderServiceTest {
    @InjectMocks
    private HtmlShopReaderService greenBirdService =
            new HtmlShopReaderServiceBean("windows-1251", "table", "class", "visit_history", "tr", "td", 4);

    @InjectMocks
    private HtmlShopReaderService pandahallService =
            new HtmlShopReaderServiceBean("utf-8", "div", "class", "order_list", "ul", "li,img", 11);

    @InjectMocks
    private HtmlShopReaderService stilnayaService =
            new HtmlShopReaderServiceBean("utf-8", "table", "class", "vmbs-order-materials", "tr", "td", 7);

    @InjectMocks
    private HtmlShopReaderService luxfurnituraService =
            new HtmlShopReaderServiceBean("utf-8", "table", "id", "purchases", "tr", "td", 5);

    @Test
    @SneakyThrows
    public void greenBirdParseTest() throws Exception {
        final Shop shop = Shop.GREEN_BIRD;
        URL resource = HtmlShopReaderServiceTest.class.getResource("/orders/" + shop.getId());
        File[] files = new File(resource.toURI()).listFiles();

        MaterialOrder actual = greenBirdService.parse(files[0], shop).orElse(null);

        assertNotNull(actual);
        assertEquals("greenBird_16.01.2019", actual.getName());
        assertEquals(14, actual.getMaterials().size());
        assertEquals(0.0, actual.getDeliveryPrice());
        assertEquals(shop, actual.getShop());
    }

    @Test
    @SneakyThrows
    public void luxfurnituraParseTest() throws Exception {
        final Shop shop = Shop.LUXFURNITURA;
        URL resource = HtmlShopReaderServiceTest.class.getResource("/orders/" + shop.getId());
        File[] files = new File(resource.toURI()).listFiles();

        MaterialOrder actual = luxfurnituraService.parse(files[0], shop).orElse(null);

        assertNotNull(actual);
        assertEquals("luxfurnitura_14.06.2022", actual.getName());
        assertEquals(24, actual.getMaterials().size());
        assertEquals(290.0, actual.getDeliveryPrice());
        assertEquals(shop, actual.getShop());
    }

    @Test
    @SneakyThrows
    public void pandahallParseTest() throws Exception {
        final Shop shop = Shop.PANDAHALL;
        URL resource = HtmlShopReaderServiceTest.class.getResource("/orders/" + shop.getId());
        File[] files = new File(resource.toURI()).listFiles();

        MaterialOrder actual = pandahallService.parse(files[0], shop).orElse(null);

        assertNotNull(actual);
        assertEquals("pandahall_23.02.2019", actual.getName());
        assertEquals(28, actual.getMaterials().size());
        assertEquals(1555.49, actual.getDeliveryPrice());
        assertEquals(shop, actual.getShop());
    }

    @Test
    @SneakyThrows
    public void stilnayaParseTest() throws Exception {
        final Shop shop = Shop.STILNAYA;
        URL resource = HtmlShopReaderServiceTest.class.getResource("/orders/" + shop.getId());
        File[] files = new File(resource.toURI()).listFiles();

        MaterialOrder actual = pandahallService.parse(files[0], shop).orElse(null);

        assertNotNull(actual);
        assertEquals("stilnaya_27.01.2019", actual.getName());
        assertEquals(0, actual.getMaterials().size());
        assertEquals(0.0, actual.getDeliveryPrice());
        assertEquals(shop, actual.getShop());
    }
}
