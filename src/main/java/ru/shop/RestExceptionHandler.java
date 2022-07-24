package ru.shop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.shop.controllers.RestResponse;

import java.util.Arrays;
import java.util.stream.Collectors;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(SPException.class)
    public ResponseEntity<RestResponse<String>> handleAllException(SPException exception) {
        if (exception.getMessage() != null) {
            log.error(exception.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(RestResponse.error(exception.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestResponse<String>> handleAllException(Exception exception) {
        log.error(exception.getMessage());
        logStackTrace(exception);
        return ResponseEntity.status(HttpStatus.OK)
                .body(RestResponse.error(exception.getMessage()));
    }

    private void logStackTrace(Exception exception) {
        if (exception.getStackTrace() != null) {
            log.error(Arrays.stream(exception.getStackTrace())
                    .map(StackTraceElement::toString).collect(Collectors.joining("\n"))
            );
        }
    }
}
