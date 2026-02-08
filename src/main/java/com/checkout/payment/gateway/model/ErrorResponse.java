package com.checkout.payment.gateway.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
  private final String message;
  private final List<ValidationError> errors;

  public ErrorResponse(String message) {
    this.message = message;
    this.errors = null;
  }

  public ErrorResponse(String message, List<ValidationError> errors) {
    this.message = message;
    this.errors = errors;
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
        "message='" + message + '\'' +
        ", errors=" + errors +
        '}';
  }
}
