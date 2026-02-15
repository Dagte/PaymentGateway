package com.checkout.payment.gateway.api.dto;

import com.checkout.payment.gateway.api.validation.ValidPaymentExpiry;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

import static com.checkout.payment.gateway.api.validation.ValidationErrorMessages.CARD_NUMBER_INVALID_SIZE;
import static com.checkout.payment.gateway.api.validation.ValidationErrorMessages.CARD_NUMBER_NUMERIC;
import static com.checkout.payment.gateway.api.validation.ValidationErrorMessages.CURRENCY_INVALID;
import static com.checkout.payment.gateway.api.validation.ValidationErrorMessages.CVV_INVALID_SIZE;
import static com.checkout.payment.gateway.api.validation.ValidationErrorMessages.CVV_NUMERIC;

@ValidPaymentExpiry
public class PostPaymentRequest implements Serializable {

  @NotBlank
  @Size(min = 14, max = 19, message = CARD_NUMBER_INVALID_SIZE)
  @Pattern(regexp = "^\\d+$", message = CARD_NUMBER_NUMERIC)
  private String cardNumber;

  @Min(1)
  @Max(12)
  private int expiryMonth;

  @NotNull
  @Positive
  private int expiryYear;

  @NotBlank
  @Pattern(regexp = "^(USD|GBP|EUR)$", message = CURRENCY_INVALID)
  private String currency;

  @NotNull
  @Positive
  private int amount;

  @NotBlank
  @Size(min = 3, max = 4, message = CVV_INVALID_SIZE)
  @Pattern(regexp = "^\\d+$", message = CVV_NUMERIC)
  private String cvv;

  public String getCardNumber() {
    return cardNumber;
  }

  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
  }

  public int getExpiryMonth() {
    return expiryMonth;
  }

  public void setExpiryMonth(int expiryMonth) {
    this.expiryMonth = expiryMonth;
  }

  public int getExpiryYear() {
    return expiryYear;
  }

  public void setExpiryYear(int expiryYear) {
    this.expiryYear = expiryYear;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  public String getCvv() {
    return cvv;
  }

  public void setCvv(String cvv) {
    this.cvv = cvv;
  }

  @JsonProperty("expiry_date")
  public String getExpiryDate() {
    return String.format("%02d/%d", expiryMonth, expiryYear);
  }

  @Override
  public String toString() {
    return "PostPaymentRequest{" +
        "cardNumber='" + (cardNumber != null ? "****" + cardNumber.substring(Math.max(0, cardNumber.length() - 4)) : "null") + '\'' +
        ", expiryMonth=" + expiryMonth +
        ", expiryYear=" + expiryYear +
        ", currency='" + currency + '\'' +
        ", amount=" + amount +
        ", cvv='***'" +
        '}';
  }
}
