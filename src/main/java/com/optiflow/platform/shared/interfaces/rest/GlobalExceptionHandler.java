package com.optiflow.platform.shared.interfaces.rest;

import com.optiflow.platform.shared.exceptions.DomainException;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiError> handleInvalidParameter(MethodArgumentTypeMismatchException exception) {
    String message = UUID.class.equals(exception.getRequiredType())
        ? "El parámetro " + exception.getName() + " debe ser un UUID válido."
        : "El parámetro " + exception.getName() + " tiene un formato inválido.";
    return ResponseEntity.badRequest()
        .body(new ApiError(400, "Bad Request", message));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiError> handleUnreadableRequest(HttpMessageNotReadableException exception) {
    return ResponseEntity.badRequest()
        .body(new ApiError(400, "Bad Request",
            "El cuerpo de la solicitud debe contener un JSON válido con los datos en el formato esperado."));
  }
}
