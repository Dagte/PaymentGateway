package com.checkout.payment.gateway.api.exception;

import static com.checkout.payment.gateway.api.validation.ValidationErrorMessages.BANK_SERVICE_UNAVAILABLE;
import static com.checkout.payment.gateway.api.validation.ValidationErrorMessages.BANK_TIMEOUT;
import static com.checkout.payment.gateway.api.validation.ValidationErrorMessages.BANK_UPSTREAM_ERROR;
import static com.checkout.payment.gateway.api.validation.ValidationErrorMessages.INTERNAL_ERROR;
import static com.checkout.payment.gateway.api.validation.ValidationErrorMessages.MALFORMED_JSON;
import static com.checkout.payment.gateway.api.validation.ValidationErrorMessages.VALIDATION_FAILED;

import com.checkout.payment.gateway.api.dto.ErrorResponse;
import com.checkout.payment.gateway.common.exception.BankTimeoutException;
import com.checkout.payment.gateway.common.exception.BankUnavailableException;
import com.checkout.payment.gateway.common.exception.BankUpstreamErrorException;
import com.checkout.payment.gateway.common.exception.EventProcessingException;
import com.checkout.payment.gateway.common.exception.PaymentNotFoundException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import java.util.List;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CommonExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(CommonExceptionHandler.class);

  @ExceptionHandler(PaymentNotFoundException.class)
  public ResponseEntity<ErrorResponse> handlePaymentNotFoundException(PaymentNotFoundException ex) {
    LOG.error("Payment not found: {}", ex.getMessage());
    return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(BankUnavailableException.class)
  public ResponseEntity<ErrorResponse> handleBankUnavailableException(BankUnavailableException ex) {
    LOG.error("Bank unavailable: {}", ex.getMessage());
    return new ResponseEntity<>(new ErrorResponse(BANK_SERVICE_UNAVAILABLE), HttpStatus.SERVICE_UNAVAILABLE);
  }

  @ExceptionHandler(CallNotPermittedException.class)
  public ResponseEntity<ErrorResponse> handleCallNotPermittedException(CallNotPermittedException ex) {
    LOG.error("Circuit breaker is open: {}", ex.getMessage());
    return new ResponseEntity<>(new ErrorResponse(BANK_SERVICE_UNAVAILABLE), HttpStatus.SERVICE_UNAVAILABLE);
  }

  @ExceptionHandler(BankTimeoutException.class)
  public ResponseEntity<ErrorResponse> handleBankTimeoutException(BankTimeoutException ex) {
    LOG.error("Bank timeout: {}", ex.getMessage());
    return new ResponseEntity<>(new ErrorResponse(BANK_TIMEOUT), HttpStatus.GATEWAY_TIMEOUT);
  }

  @ExceptionHandler(BankUpstreamErrorException.class)
  public ResponseEntity<ErrorResponse> handleBankUpstreamErrorException(BankUpstreamErrorException ex) {
    LOG.error("Bank upstream error: {}", ex.getMessage());
    return new ResponseEntity<>(new ErrorResponse(BANK_UPSTREAM_ERROR), HttpStatus.BAD_GATEWAY);
  }

  @ExceptionHandler(EventProcessingException.class)
  public ResponseEntity<ErrorResponse> handleException(EventProcessingException ex) {
    LOG.error("Event processing exception: {}", ex.getMessage());
    return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
    List<ErrorResponse.ValidationError> errors = Stream.concat(
        ex.getBindingResult().getFieldErrors().stream()
            .map(error -> new ErrorResponse.ValidationError(error.getField(), error.getDefaultMessage())),
        ex.getBindingResult().getGlobalErrors().stream()
            .map(error -> new ErrorResponse.ValidationError(error.getObjectName(), error.getDefaultMessage()))
    ).toList();

    LOG.error("Validation failed: {}", errors);
    return new ResponseEntity<>(new ErrorResponse(VALIDATION_FAILED, errors), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleMalformedJsonException(HttpMessageNotReadableException ex) {
    LOG.error("Malformed JSON request: {}", ex.getMessage());
    return new ResponseEntity<>(new ErrorResponse(MALFORMED_JSON), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
    LOG.error("Unexpected error occurred", ex);
    return new ResponseEntity<>(new ErrorResponse(INTERNAL_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}