package ru.shop;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.shop.service.parser.HtmlShopReaderService;
import ru.shop.service.parser.HtmlShopReaderServiceBean;

@Configuration
public class AppConfig {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public HtmlShopReaderService greenBirdParser() {
        return new HtmlShopReaderServiceBean("windows-1251", "table", "class", "visit_history", "tr", "td", 4);
    }

    @Bean
    public HtmlShopReaderService pandahallParser() {
        return new HtmlShopReaderServiceBean("utf-8", "div", "class", "order_list", "ul", "li,img", 11);
    }

    @Bean
    public HtmlShopReaderService stilnayaParser() {
        return new HtmlShopReaderServiceBean("utf-8", "table", "class", "vmbs-order-materials", "tr", "td", 7);
    }

    @Bean
    public HtmlShopReaderService luxfurnituraParser() {
        return new HtmlShopReaderServiceBean("utf-8", "table", "id", "purchases", "tr", "td", 5);
    }
}
