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

  public PaymentGatewayService(PaymentsRepository paymentsRepository) {
    this.paymentsRepository = paymentsRepository;
  }

  public Payment getPaymentById(UUID id) {
    LOG.debug("Requesting access to to payment with ID {}", id);
    return paymentsRepository.get(id).orElseThrow(() -> new EventProcessingException("Invalid ID"));
  }

  public Payment processPayment(Payment payment) {
    LOG.debug("Processing payment domain object: {}", payment);
    
    // For now, still using a stub until we finish the bank client wiring
    payment.setId(UUID.randomUUID());
    payment.setStatus(com.checkout.payment.gateway.common.enums.PaymentStatus.AUTHORIZED);
    
    paymentsRepository.add(payment);
    return payment;
  }
}