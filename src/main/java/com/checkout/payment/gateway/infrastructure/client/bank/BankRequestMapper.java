package com.checkout.payment.gateway.infrastructure.client.bank;

import com.checkout.payment.gateway.core.model.Payment;
import com.checkout.payment.gateway.infrastructure.client.bank.model.BankPaymentRequest;

public final class BankRequestMapper {

  private BankRequestMapper() {
  }

  public static BankPaymentRequest mapToBankRequest(Payment domain, String cardNumber, String cvv) {
    String expiryDate = String.format("%02d/%d", domain.getExpiryMonth(), domain.getExpiryYear());
    return new BankPaymentRequest(
        cardNumber,
        expiryDate,
        domain.getCurrency(),
        domain.getAmount(),
        cvv
    );
  }
}