package ru.shop;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "shop-price.jewelry-shop")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JewelryShopProperties {
    private String address;
    private String allJewelryUrl;
}
