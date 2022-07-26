package ru.shop.service;

import com.google.common.util.concurrent.UncheckedExecutionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import ru.shop.JewelryShopProperties;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class JewelryServiceTest {
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private JewelryShopProperties jewelryShopProperties;
    @InjectMocks
    private JewelryServiceImpl service;

    @Test
    public void getAllJewelriesTest() {
        Assertions.assertThrows(UncheckedExecutionException.class, () -> service.getAllJewelries(),
                "Could not load jewelries from Shop Service");

        verify(jewelryShopProperties).getAddress();
        verify(jewelryShopProperties).getAllJewelryUrl();
    }
}
