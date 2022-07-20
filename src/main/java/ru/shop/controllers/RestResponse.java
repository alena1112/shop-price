package ru.shop.controllers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestResponse<T> {
    public static final RestResponse<String> OK = new RestResponse<>("OK", null);

    private T data;
    private String error;

    public static RestResponse<String> ok() {
        return OK;
    }

    public static <T> RestResponse<T> error(String errorMessage) {
        RestResponse<T> response = new RestResponse<>();
        response.setError(errorMessage);
        return response;
    }

    public static <T> RestResponse<T> withData(T data) {
        return new RestResponse<>(data, null);
    }
}
