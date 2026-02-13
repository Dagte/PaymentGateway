package com.checkout.payment.gateway.core.service;

import com.checkout.payment.gateway.common.enums.PaymentStatus;
import com.checkout.payment.gateway.common.exception.PaymentNotFoundException;
import com.checkout.payment.gateway.core.model.Payment;
import com.checkout.payment.gateway.infrastructure.persistence.PaymentsRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PaymentGatewayService {

  private final PaymentsRepository paymentsRepository;
  private final AcquiringBankClient bankClient;

  public PaymentGatewayService(PaymentsRepository paymentsRepository,
      AcquiringBankClient bankClient) {
    this.paymentsRepository = paymentsRepository;
    this.bankClient = bankClient;
  }

  public Payment getPaymentById(UUID id) {
    return paymentsRepository.get(id).orElseThrow(() -> new PaymentNotFoundException(id));
  }

  public PaymentProcessResult processPayment(Payment payment, String cardNumber, String cvv,
      String idempotencyKey) {
    boolean isRetry = idempotencyKey != null && paymentsRepository.findIdByIdempotencyKey(idempotencyKey).isPresent();
    var activePayment = getOrCreatePayment(payment, idempotencyKey);

    if (activePayment.getStatus() != PaymentStatus.PENDING) {
      return new PaymentProcessResult(activePayment, isRetry);
    }

    PaymentStatus paymentStatus = bankClient.process(activePayment, cardNumber, cvv);
    activePayment.setStatus(paymentStatus);
    paymentsRepository.add(activePayment);
    return new PaymentProcessResult(activePayment, isRetry);
  }

  private Payment getOrCreatePayment(Payment payment, String idempotencyKey) {
    if (idempotencyKey != null) {
      // TODO: This check-then-act sequence is not atomic. In a multi-node environment, 
      // use a distributed lock or atomic computeIfAbsent to prevent duplicate bank calls.
      var existingId = paymentsRepository.findIdByIdempotencyKey(idempotencyKey);

      if (existingId.isPresent()) {
        return paymentsRepository.get(existingId.get())
            .orElseThrow(() -> new IllegalStateException("Index exists but Payment missing"));
      }
    }

    return initializeNewPayment(payment, idempotencyKey);
  }

  private Payment initializeNewPayment(Payment payment, String idempotencyKey) {
    payment.setId(UUID.randomUUID());
    payment.setStatus(PaymentStatus.PENDING);
    paymentsRepository.add(payment);

    if (idempotencyKey != null) {
      paymentsRepository.saveIdempotencyKey(idempotencyKey, payment.getId());
    }
    return payment;
  }

}
