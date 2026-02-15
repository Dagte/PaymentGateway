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

  public PaymentProcessResult processPayment(Payment requestedPayment, String cardNumber, String cvv,
      String idempotencyKey) {
    PaymentProcessResult result = getOrCreatePayment(requestedPayment, idempotencyKey);
    Payment activePayment = result.payment();

    synchronized (activePayment) {
      if (activePayment.getStatus() != PaymentStatus.PENDING) {
        return result;
      }

      PaymentStatus paymentStatus = bankClient.process(activePayment, cardNumber, cvv);
      activePayment.setStatus(paymentStatus);
      paymentsRepository.add(activePayment);
      return new PaymentProcessResult(activePayment, result.isRetry());
    }
  }

  private PaymentProcessResult getOrCreatePayment(Payment requestedPayment, String idempotencyKey) {
    if (idempotencyKey != null) {
      Payment finalPayment = paymentsRepository.getOrCreate(idempotencyKey, () -> {
        initializeNewPayment(requestedPayment);
        return requestedPayment;
      });

      boolean isRetry = requestedPayment.getId() == null;
      return new PaymentProcessResult(finalPayment, isRetry);
    }

    initializeNewPayment(requestedPayment);
    paymentsRepository.add(requestedPayment);
    return new PaymentProcessResult(requestedPayment, false);
  }

  private static void initializeNewPayment(Payment requestedPayment) {
    requestedPayment.setId(UUID.randomUUID());
    requestedPayment.setStatus(PaymentStatus.PENDING);
  }

}