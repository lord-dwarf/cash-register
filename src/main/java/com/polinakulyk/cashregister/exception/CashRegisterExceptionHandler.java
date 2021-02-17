package com.polinakulyk.cashregister.exception;

import com.polinakulyk.cashregister.exception.dto.ErrorDto;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CashRegisterExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ResponseStatusException.class})
    protected ResponseEntity<Object> handleConstraintViolationException(
            ResponseStatusException e, WebRequest request) {
        String errorMessage = retrieveErrorMessageCause(e.getMessage());
        ErrorDto body = new ErrorDto(errorMessage);
        return handleExceptionInternal(e, body, new HttpHeaders(), e.getStatus(), request);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    protected ResponseEntity<Object> handleConstraintViolationException(
            ConstraintViolationException e, WebRequest request) {
        String errorMessage = ((ConstraintViolation)(e.getConstraintViolations().toArray()[0]))
                .getMessage();
        ErrorDto body = new ErrorDto(errorMessage);
        return handleExceptionInternal(e, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        String errorMessage = e.getAllErrors().get(0).toString();
        ErrorDto body = new ErrorDto(errorMessage);
        return handleExceptionInternal(e, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({Throwable.class})
    protected ResponseEntity<Object> handleThrowable(
            Throwable t, WebRequest request) {
        String errorMessage = retrieveErrorMessageCause(t.getMessage());
        ErrorDto body = new ErrorDto(errorMessage);
        return new ResponseEntity<>(body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception e,
            @Nullable Object body,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        if (body != null) {
            return new ResponseEntity<>(body, headers, status);
        } else {
            return new ResponseEntity<>(retrieveErrorMessageCause(e.getMessage()), headers, status);
        }
    }

    private String retrieveErrorMessageCause(String errorMessage) {
        int nestedExceptionMessagePos = errorMessage.indexOf("nested exception is");
        if (nestedExceptionMessagePos >= 0) {
            errorMessage = errorMessage.substring(0, nestedExceptionMessagePos);
        }
        return errorMessage;
    }
}
