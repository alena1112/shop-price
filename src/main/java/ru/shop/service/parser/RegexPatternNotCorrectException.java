package ru.shop.service.parser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegexPatternNotCorrectException extends RuntimeException {
    private final String pattern;
    private final String shop;
    private final String line;

    @Override
    public String getMessage() {
        return String.format("Pattern %s is not correct for shop %s, line: %s", pattern, shop, line);
    }
}
