package com.checkout.payment.gateway.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.checkout.payment.gateway.common.enums.PaymentStatus;
import com.checkout.payment.gateway.common.exception.PaymentNotFoundException;
import com.checkout.payment.gateway.core.model.Payment;
import com.checkout.payment.gateway.infrastructure.persistence.PaymentsRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentGatewayServiceTest {

  @Mock
  private PaymentsRepository paymentsRepository;

  @Mock
  private AcquiringBankClient bankClient;

  private PaymentGatewayService paymentGatewayService;

  @BeforeEach
  void setUp() {
    paymentGatewayService = new PaymentGatewayService(paymentsRepository, bankClient);
  }

  @Test
  void whenProcessPaymentWithoutKeyThenAssignsIdCallsBankAndStoresInRepository() {
    Payment payment = new Payment();
    payment.setAmount(100);
    String cardNumber = "1234567890123456";
    String cvv = "123";

    when(bankClient.process(any(Payment.class), eq(cardNumber), eq(cvv)))
        .thenReturn(PaymentStatus.AUTHORIZED);

    PaymentProcessResult result = paymentGatewayService.processPayment(payment, cardNumber, cvv, null);

    assertThat(result.payment().getId()).isNotNull();
    assertThat(result.payment().getStatus()).isEqualTo(PaymentStatus.AUTHORIZED);
    assertThat(result.isRetry()).isFalse();
    
    verify(bankClient).process(payment, cardNumber, cvv);
    verify(paymentsRepository, times(2)).add(payment);
  }

  @Test
  void whenProcessPaymentWithExistingKeyThenReturnsCachedPayment() {
    String key = "test-key";
    UUID id = UUID.randomUUID();
    Payment existingPayment = new Payment();
    existingPayment.setId(id);
    existingPayment.setStatus(PaymentStatus.AUTHORIZED);

    when(paymentsRepository.getOrCreate(eq(key), any())).thenReturn(existingPayment);

    PaymentProcessResult result = paymentGatewayService.processPayment(new Payment(), "1234", "123", key);

    assertThat(result.payment()).isEqualTo(existingPayment);
    assertThat(result.isRetry()).isTrue();
    verify(bankClient, never()).process(any(), any(), any());
  }

  @Test
  void whenGetPaymentByIdWithValidIdThenReturnsPayment() {
    UUID id = UUID.randomUUID();
    Payment payment = new Payment();
    payment.setId(id);
    when(paymentsRepository.get(id)).thenReturn(Optional.of(payment));

    Payment result = paymentGatewayService.getPaymentById(id);

    assertThat(result).isEqualTo(payment);
    verify(paymentsRepository).get(id);
  }

  @Test
  void whenGetPaymentByIdWithInvalidIdThenThrowsException() {
    UUID id = UUID.randomUUID();
    when(paymentsRepository.get(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> paymentGatewayService.getPaymentById(id))
        .isInstanceOf(PaymentNotFoundException.class)
        .hasMessage("Payment not found for ID: " + id);
  }
}
