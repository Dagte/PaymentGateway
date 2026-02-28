package com.checkout.payment.gateway.infrastructure.client.bank;

import com.checkout.payment.gateway.core.model.Payment;
import com.checkout.payment.gateway.infrastructure.client.bank.model.BankPaymentRequest;

public final class BankRequestMapper {

  private BankRequestMapper() {
  }

  public static BankPaymentRequest mapToBankRequest(Payment payment, String cardNumber, String cvv) {
    String expiryDate = String.format("%02d/%d", payment.getExpiryMonth(), payment.getExpiryYear());
    return new BankPaymentRequest(
        cardNumber,
        expiryDate,
        payment.getCurrency(),
        payment.getAmount(),
        cvv,
        payment.getId().toString()
    );
  }
}