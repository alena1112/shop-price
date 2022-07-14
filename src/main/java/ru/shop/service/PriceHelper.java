package ru.shop.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PriceHelper {
    public static double round(double value) {
        BigDecimal result = new BigDecimal(value);
        result = result.setScale(2, RoundingMode.HALF_UP);
        return result.doubleValue();
    }
}
