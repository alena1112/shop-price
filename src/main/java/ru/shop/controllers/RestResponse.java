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
    private T data;
    private String message;
    private String error;

    public static RestResponse<String> ok(String message) {
        return new RestResponse<>("OK", message, null);
    }

    public static <T> RestResponse<T> error(String errorMessage) {
        RestResponse<T> response = new RestResponse<>();
        response.setError(errorMessage);
        return response;
    }

    public static <T> RestResponse<T> withData(T data, String message) {
        return new RestResponse<>(data, message, null);
    }
}
