package com.optiflow.booking.interfaces.rest;

import com.optiflow.booking.domain.exception.DomainException;
import com.optiflow.booking.interfaces.rest.dto.ApiError;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DomainException.class)
  public ResponseEntity<ApiError> handleDomain(DomainException exception) {
    HttpStatus status = HttpStatus.resolve(exception.status());
    if (status == null) {
      status = HttpStatus.BAD_REQUEST;
    }
    return ResponseEntity.status(status)
        .body(new ApiError(status.value(), status.getReasonPhrase(), exception.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception) {
    String message = exception.getBindingResult().getFieldErrors().stream()
        .map(error -> error.getField() + " " + error.getDefaultMessage())
        .collect(Collectors.joining("; "));
    return ResponseEntity.badRequest()
        .body(new ApiError(400, "Bad Request", message));
  }
}
