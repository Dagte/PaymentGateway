package com.checkout.payment.gateway.api.controller;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.checkout.payment.gateway.common.enums.PaymentStatus;
import com.checkout.payment.gateway.core.model.Payment;
import com.checkout.payment.gateway.infrastructure.persistence.PaymentsRepository;
import com.checkout.payment.gateway.support.BasePaymentGatewayTest;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PaymentGatewayControllerTest extends BasePaymentGatewayTest {

  @Autowired
  private PaymentsRepository paymentsRepository;

  @Test
  void whenPaymentWithIdExistThenCorrectPaymentIsReturned() throws Exception {
    Payment payment = new Payment();
    payment.setId(UUID.randomUUID());
    payment.setAmount(10);
    payment.setCurrency("USD");
    payment.setStatus(PaymentStatus.AUTHORIZED);
    payment.setExpiryMonth(12);
    payment.setExpiryYear(2024);
    payment.setCardNumberLastFour("0021");

    paymentsRepository.add(payment);

    client.get("/api/payment/" + payment.getId())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(payment.getStatus().getName()))
        .andExpect(jsonPath("$.cardNumberLastFour").value("0021"))
        .andExpect(jsonPath("$.expiryMonth").value(payment.getExpiryMonth()))
        .andExpect(jsonPath("$.expiryYear").value(payment.getExpiryYear()))
        .andExpect(jsonPath("$.currency").value(payment.getCurrency()))
        .andExpect(jsonPath("$.amount").value(payment.getAmount()));
  }

  @Test
  void whenPaymentWithIdDoesNotExistThen404IsReturned() throws Exception {
    client.get("/api/payment/" + UUID.randomUUID())
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Invalid ID"));
  }
}
