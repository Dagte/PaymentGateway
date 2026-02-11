package com.checkout.payment.gateway.api.controller;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.checkout.payment.gateway.BasePaymentGatewayTest;
import com.checkout.payment.gateway.common.enums.PaymentStatus;
import com.checkout.payment.gateway.core.model.Payment;
import com.checkout.payment.gateway.infrastructure.persistence.PaymentsRepository;
import com.checkout.payment.gateway.api.dto.PostPaymentRequest;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class PaymentGatewayControllerTest extends BasePaymentGatewayTest {

  @Autowired
  private PaymentsRepository paymentsRepository;

  @Autowired
  private RestTemplate restTemplate;

  @Value("${bank.simulator.url}")
  private String bankUrl;

  private MockRestServiceServer mockBankServer;

  @BeforeEach
  @Override
  protected void setUp() {
    super.setUp();
    mockBankServer = MockRestServiceServer.createServer(restTemplate);
  }

  @Test
  void whenPaymentProcessedWithIdempotencyKeyThenSubsequentRequestsReturnSameResponse() throws Exception {
    PostPaymentRequest request = createValidRequest();
    String idempotencyKey = UUID.randomUUID().toString();

    mockBankServer.expect(requestTo(bankUrl))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withSuccess(BANK_AUTHORIZED_RESPONSE_JSON, MediaType.APPLICATION_JSON));

    MvcResult firstResult = client.post("/api/payments", request, idempotencyKey)
        .andExpect(status().isCreated())
        .andReturn();

    MvcResult secondResult = client.post("/api/payments", request, idempotencyKey)
        .andExpect(status().isOk())
        .andReturn();

    String firstResponse = firstResult.getResponse().getContentAsString();
    String secondResponse = secondResult.getResponse().getContentAsString();

    assertThat(firstResponse).isEqualTo(secondResponse);
    mockBankServer.expect(ExpectedCount.once(), requestTo(bankUrl));
    mockBankServer.verify();
  }

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
    UUID id = UUID.randomUUID();
    client.get("/api/payment/" + id)
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Payment not found for ID: " + id));
  }
}