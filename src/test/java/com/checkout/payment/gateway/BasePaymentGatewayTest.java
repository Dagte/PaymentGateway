package com.checkout.payment.gateway;

import com.checkout.payment.gateway.api.dto.PostPaymentRequest;
import com.checkout.payment.gateway.client.PaymentGatewayTestClient;
import com.checkout.payment.gateway.infrastructure.persistence.PaymentsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class BasePaymentGatewayTest {

  protected static final String BANK_UNAUTHORIZED_RESPONSE_JSON = "{\"authorized\": false}";
  protected static final String BANK_AUTHORIZED_RESPONSE_JSON = "{\"authorized\": true, \"authorization_code\": \"auth-123\"}";

  @Autowired
  protected MockMvc mvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @Autowired
  protected PaymentsRepository paymentsRepository;

  @Autowired
  protected CircuitBreakerRegistry circuitBreakerRegistry;

  protected PaymentGatewayTestClient client;

  @BeforeEach
  protected void setUp() {
    circuitBreakerRegistry.circuitBreaker("bankCircuitBreaker").transitionToClosedState();
    client = new PaymentGatewayTestClient(mvc, objectMapper);
  }

  protected PostPaymentRequest createValidRequest() {
    PostPaymentRequest request = new PostPaymentRequest();
    request.setCardNumber("1234567890123456");
    request.setExpiryMonth(12);
    request.setExpiryYear(2030);
    request.setCurrency("USD");
    request.setAmount(1000);
    request.setCvv("123");
    return request;
  }
}
