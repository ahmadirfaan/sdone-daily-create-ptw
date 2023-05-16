package com.sdone.createdailyptw.controller;

import com.sdone.createdailyptw.exception.GrpcClientException;
import com.sdone.createdailyptw.exception.InvalidDataPtwException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {


    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleException(status, "Required request body is missing");
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        BindingResult result = ex.getBindingResult();
        String builder;
        var fieldErrors = result.getFieldErrors();

        builder = fieldErrors.stream()
                .map(error -> error.getField() + " : " + error.getDefaultMessage())
                .collect(Collectors.joining());
        return handleException(HttpStatus.BAD_REQUEST, builder);
    }


    @ExceptionHandler(GrpcClientException.class)
    public ResponseEntity<Object> handleAuthenticationException(GrpcClientException ex) {
        logger.error("GrpcClientException : ", ex);
        return handleException(ex.getStatus(), ex.getReason());
    }

    @ExceptionHandler(InvalidDataPtwException.class)
    public ResponseEntity<Object> handleAuthenticationException(InvalidDataPtwException ex) {
        logger.error("InvalidDataPtwException : ", ex);
        return handleException(ex.getStatus(), ex.getReason());
    }


    private ResponseEntity<Object> handleException(HttpStatus status, String message) {
        Map<String, String> body = new HashMap<>();
        body.put("result", "error");
        body.put("msg", message);
        return ResponseEntity.status(status.value()).body(body);
    }
}
