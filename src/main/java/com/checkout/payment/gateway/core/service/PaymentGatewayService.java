package com.checkout.payment.gateway.core.service;

import com.checkout.payment.gateway.common.exception.EventProcessingException;
import com.checkout.payment.gateway.core.model.Payment;
import com.checkout.payment.gateway.infrastructure.persistence.PaymentsRepository;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentGatewayService {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayService.class);

  private final PaymentsRepository paymentsRepository;
  private final AcquiringBankClient bankClient;

  public PaymentGatewayService(PaymentsRepository paymentsRepository, AcquiringBankClient bankClient) {
    this.paymentsRepository = paymentsRepository;
    this.bankClient = bankClient;
  }

  public Payment getPaymentById(UUID id) {
    return paymentsRepository.get(id).orElseThrow(() -> new EventProcessingException("Invalid ID"));
  }

  public Payment processPayment(Payment payment) {
    payment.setId(UUID.randomUUID());
    bankClient.process(payment);
    paymentsRepository.add(payment);
    return payment;
  }
}