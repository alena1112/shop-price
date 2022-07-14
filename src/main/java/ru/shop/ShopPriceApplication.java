package ru.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ShopPriceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShopPriceApplication.class, args);
    }
}
