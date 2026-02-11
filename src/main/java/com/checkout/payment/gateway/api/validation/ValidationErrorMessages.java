package com.checkout.payment.gateway.api.validation;

public final class ValidationErrorMessages {

  public static final String CARD_NUMBER_NUMERIC = "Card number must only contain numeric characters";
  public static final String CARD_NUMBER_INVALID_SIZE = "Card number must be between 14 and 19 characters";
  public static final String CVV_NUMERIC = "CVV must only contain numeric characters";
  public static final String CVV_INVALID_SIZE = "CVV must be between 3 and 4 characters";
  public static final String CURRENCY_INVALID = "Currency must be one of: USD, GBP, EUR";
  public static final String EXPIRY_DATE_PAST = "The card expiry date must be in the future";
  public static final String VALIDATION_FAILED = "Validation failed";
  public static final String MALFORMED_JSON = "Malformed JSON request";
  public static final String INTERNAL_ERROR = "An internal error occurred";
  public static final String BANK_SERVICE_UNAVAILABLE = "Acquiring bank is currently unavailable";

  private ValidationErrorMessages() {
    // Prevent instantiation
  }
}