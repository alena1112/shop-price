package ru.shop.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.shop.JewelryShopProperties;
import ru.shop.SPException;
import ru.shop.controllers.dto.JewelryDto;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class JewelryServiceBean implements JewelryService {
    private static final Logger log = LoggerFactory.getLogger(JewelryServiceBean.class);

    private final RestTemplate restTemplate;
    private final JewelryShopProperties jewelryShopProperties;

    private LoadingCache<String, List<JewelryDto>> JEWELRIES_CACHE;

    @PostConstruct
    public void initCache() {
        JEWELRIES_CACHE = CacheBuilder
                .newBuilder()
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .maximumSize(10000)
                .build(new CacheLoader<>() {
                    @Override
                    public List<JewelryDto> load(String key) {
                        return loadJewelriesFromService();
                    }
                });
    }

    @Override
    public List<JewelryDto> getAllJewelries() {
        return JEWELRIES_CACHE.getUnchecked("ANY");
    }

    private List<JewelryDto> loadJewelriesFromService() {
        log.info("start loading jewelries from jewelry shop service");
        List<JewelryDto> allJewelries;
        String address = jewelryShopProperties.getAddress() + jewelryShopProperties.getAllJewelryUrl();
        try {
            ResponseEntity<List<JewelryDto>> responseEntity =
                    restTemplate.exchange(
                            address,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {
                            }
                    );
            allJewelries = responseEntity.getBody();
        } catch (Exception e) {
            throw new SPException("Could not load jewelries from Shop Service");
        }
        if (allJewelries != null) {
            log.info("loaded {} jewelries", allJewelries.size());
            allJewelries.forEach(j -> j.setImageUrl(jewelryShopProperties.getAddress() + j.getImageUrl()));
        } else {
            log.error("Could not load jewelries from jewelry shop service, url {}", address);
        }
        return allJewelries;
    }
}
