package com.checkout.payment.gateway.support;

import com.checkout.payment.gateway.api.dto.PostPaymentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class BasePaymentGatewayTest {

  @Autowired
  protected MockMvc mvc;

  @Autowired
  protected ObjectMapper objectMapper;

  protected PaymentGatewayTestClient client;

  @BeforeEach
  void setUp() {
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
