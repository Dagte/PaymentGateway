package com.checkout.payment.gateway.core.service;

import com.checkout.payment.gateway.common.enums.PaymentStatus;
import com.checkout.payment.gateway.core.model.Payment;

public interface AcquiringBankClient {
  PaymentStatus process(Payment payment, String cardNumber, String cvv);
}