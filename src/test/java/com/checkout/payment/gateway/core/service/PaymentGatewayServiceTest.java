package com.checkout.payment.gateway.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
  void whenProcessPaymentThenAssignsIdCallsBankAndStoresInRepository() {
    Payment payment = new Payment();
    payment.setAmount(100);
    String cardNumber = "1234567890123456";
    String cvv = "123";

    when(bankClient.process(any(Payment.class), eq(cardNumber), eq(cvv)))
        .thenReturn(PaymentStatus.AUTHORIZED);

    Payment result = paymentGatewayService.processPayment(payment, cardNumber, cvv);

    assertThat(result.getId()).isNotNull();
    assertThat(result.getStatus()).isEqualTo(PaymentStatus.AUTHORIZED);
    
    verify(bankClient).process(payment, cardNumber, cvv);
    verify(paymentsRepository).add(payment);
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
