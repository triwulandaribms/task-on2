package jawa.sinaukoding.sk.controller;

import jawa.sinaukoding.sk.exception.ListRequestException;
import jawa.sinaukoding.sk.model.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ListRequestException.class)
    public ResponseEntity<Response<Object>> handleListRequestException(ListRequestException e, WebRequest request) {
        Response<Object> errorResponse = new Response<>("0401", e.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Response<Object>> handleRuntimeException(RuntimeException e, WebRequest request) {
        Response<Object> errorResponse = new Response<>("0301", "bad request", null);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
