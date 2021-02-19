package com.polinakulyk.cashregister.exception.handler;

import com.polinakulyk.cashregister.exception.CashRegisterException;
import com.polinakulyk.cashregister.exception.dto.ErrorDto;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
@ControllerAdvice
public class CashRegisterExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Exception handler for our base exception {@link CashRegisterException}.
     * <p>
     * The exception contains HTTP status code and exception message.
     * The result is response entity with HTTP code and response body that contains
     * error message set to exception message.
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler({CashRegisterException.class})
    protected ResponseEntity<Object> handleCashRegisterException(
            CashRegisterException e, WebRequest request) {
        String errorMessage = e.getMessage();
        ErrorDto body = new ErrorDto(errorMessage);
        return handleExceptionInternal(e, body, new HttpHeaders(), e.getStatus(), request);
    }

    /**
     * Exception handler for {@link MethodArgumentNotValidException},
     * which is thrown as a result of Spring validating via @Valid.
     * <p>
     * The exception contains at least one error object with interpolated error message.
     * The result is response entity with an appropriate HTTP code provided by
     * {@link ResponseEntityExceptionHandler} (an HTTP 400), and a response body that contains
     * error message set to the very first interpolated validation error message,
     * or to a "Bad Request" as a fallback.
     *
     * @param e
     * @param headers
     * @param status
     * @param request
     * @return
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        String errorMessage = getValidationErrorMessage(e);
        ErrorDto body = new ErrorDto(errorMessage);
        return handleExceptionInternal(e, body, headers, status, request);
    }

    /**
     * Exception handler for {@link DataIntegrityViolationException},
     * which is thrown as a result of database constraint violation during insert/update.
     * <p>
     * The result is the standard HTTP 400.
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler({DataIntegrityViolationException.class})
    protected ResponseEntity<Object> handleConstraintViolationException(
            DataIntegrityViolationException e, WebRequest request) {
        ErrorDto body = new ErrorDto("Entity already exists");
        return handleExceptionInternal(e, body, new HttpHeaders(), BAD_REQUEST, request);
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException e, WebRequest request) {
        return handleExceptionInternal(e, null, new HttpHeaders(), UNAUTHORIZED, request);
    }

    /**
     * Fallback handler.
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleGeneralException(Exception e, WebRequest request) {
        return handleExceptionInternal(
                e, null, new HttpHeaders(), INTERNAL_SERVER_ERROR, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception e,
            @Nullable Object body,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        var errorBody = ofNullable(body)
                .map((b) -> {
                    if (b instanceof ErrorDto) {
                        return (ErrorDto)b;
                    }
                    // wrap ErrorDto around given error body
                    return new ErrorDto(b.toString());
                })
                .orElseGet(() -> {
                    // null body results in response with standard error message
                    var errorMessage = status.getReasonPhrase();
                    return new ErrorDto(errorMessage);
                });
        var errorMessage = errorBody.getErrorMessage();
        var response = new ResponseEntity<>((Object)errorBody, headers, status);

        log.error(errorMessage, e);
        return response;
    }

    /*
     * Get a null-safe error message from the validation exception.
     */
    private String getValidationErrorMessage(BindException e) {
        return e.getAllErrors().stream()
                .filter(Objects::nonNull)
                .findFirst()
                .map(err -> err.getDefaultMessage())
                .orElseGet(() -> BAD_REQUEST.getReasonPhrase());
    }
}
