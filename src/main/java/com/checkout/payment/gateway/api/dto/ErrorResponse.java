package com.checkout.payment.gateway.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
  private final String status;
  private final String message;
  private final List<ValidationError> errors;

  public ErrorResponse(String message) {
    this(null, message, null);
  }

  public ErrorResponse(String status, String message) {
    this(status, message, null);
  }

  public ErrorResponse(String message, List<ValidationError> errors) {
    this(null, message, errors);
  }

  public ErrorResponse(String status, String message, List<ValidationError> errors) {
    this.status = status;
    this.message = message;
    this.errors = errors;
  }

  public String getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }

  public List<ValidationError> getErrors() {
    return errors;
  }

  public record ValidationError(String field, String message) {}

  @Override
  public String toString() {
    return "ErrorResponse{" +
        "status='" + status + '\'' +
        ", message='" + message + '\'' +
        ", errors=" + errors +
        '}';
  }
}