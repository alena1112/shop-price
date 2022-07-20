package ru.shop;

public class SPException extends RuntimeException {
    public SPException(String message) {
        super(message);
    }

    public static void checkNotEmpty(String str, String message) {
        if (str == null || str.isEmpty()) {
            throw new SPException(message);
        }
    }

    public static void checkNotNull(Object obj, String message) {
        if (obj == null) {
            throw new SPException(message);
        }
    }
}
