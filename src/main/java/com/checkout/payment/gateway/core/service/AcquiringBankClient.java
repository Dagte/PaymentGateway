package com.checkout.payment.gateway.core.service;

import com.checkout.payment.gateway.core.model.Payment;

public interface AcquiringBankClient {
  void process(Payment payment);
}
