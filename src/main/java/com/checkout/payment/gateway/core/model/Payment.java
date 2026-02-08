package com.checkout.payment.gateway.core.model;

import com.checkout.payment.gateway.common.enums.PaymentStatus;
import java.util.UUID;

public class Payment {
  private UUID id;
  private PaymentStatus status;
  private String cardNumber;
  private String cardNumberLastFour;
  private int expiryMonth;
  private int expiryYear;
  private String currency;
  private int amount;
  private String cvv;

  public Payment() {}

  public Payment(UUID id, PaymentStatus status, String cardNumber, String cardNumberLastFour, int expiryMonth, int expiryYear, String currency, int amount, String cvv) {
    this.id = id;
    this.status = status;
    this.cardNumber = cardNumber;
    this.cardNumberLastFour = cardNumberLastFour;
    this.expiryMonth = expiryMonth;
    this.expiryYear = expiryYear;
    this.currency = currency;
    this.amount = amount;
    this.cvv = cvv;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public PaymentStatus getStatus() {
    return status;
  }

  public void setStatus(PaymentStatus status) {
    this.status = status;
  }

  public String getCardNumber() {
    return cardNumber;
  }

  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
  }

  public String getCardNumberLastFour() {
    return cardNumberLastFour;
  }

  public void setCardNumberLastFour(String cardNumberLastFour) {
    this.cardNumberLastFour = cardNumberLastFour;
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
}
