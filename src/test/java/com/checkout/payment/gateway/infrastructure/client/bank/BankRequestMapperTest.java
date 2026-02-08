package com.checkout.payment.gateway.infrastructure.client.bank;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.checkout.payment.gateway.core.model.Payment;
import com.checkout.payment.gateway.infrastructure.client.bank.model.BankPaymentRequest;
import org.junit.jupiter.api.Test;

class BankRequestMapperTest {

  @Test
  void shouldMapDomainToBankRequestCorrectly() {
    Payment payment = new Payment();
    payment.setExpiryMonth(12);
    payment.setExpiryYear(2030);
    payment.setCurrency("USD");
    payment.setAmount(1000);

    BankPaymentRequest request = BankRequestMapper.mapToBankRequest(payment, "1234567890123456", "123");

    assertEquals("1234567890123456", request.cardNumber());
    assertEquals("12/2030", request.expiryDate());
    assertEquals("USD", request.currency());
    assertEquals(1000, request.amount());
    assertEquals("123", request.cvv());
  }

  @Test
  void shouldPadExpiryMonthWithZero() {
    Payment payment = new Payment();
    payment.setExpiryMonth(5);
    payment.setExpiryYear(2026);

    BankPaymentRequest request = BankRequestMapper.mapToBankRequest(payment, "1234", "123");

    assertEquals("05/2026", request.expiryDate());
  }
}