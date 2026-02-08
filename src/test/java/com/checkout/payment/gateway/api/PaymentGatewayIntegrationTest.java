package com.checkout.payment.gateway.api;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.checkout.payment.gateway.api.dto.PostPaymentRequest;
import com.checkout.payment.gateway.common.enums.PaymentStatus;
import com.checkout.payment.gateway.BasePaymentGatewayTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

class PaymentGatewayIntegrationTest extends BasePaymentGatewayTest {

  public static final String BANK_UNAUTHORIZED_RESPONSE_JSON = "{\"authorized\": false}";
  private static final String BANK_AUTHORIZED_RESPONSE_JSON = "{\"authorized\": true, \"authorization_code\": \"auth-123\"}";
  
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
  void shouldProcessSuccessfulPayment() throws Exception {
    PostPaymentRequest request = createValidRequest();

    mockBankServer.expect(requestTo(bankUrl))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withSuccess(BANK_AUTHORIZED_RESPONSE_JSON, MediaType.APPLICATION_JSON));

    client.post("/api/payments", request)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value(PaymentStatus.AUTHORIZED.getName()))
        .andExpect(jsonPath("$.cardNumberLastFour").value("3456"))
        .andExpect(jsonPath("$.id").exists());

    mockBankServer.verify();
  }

  @Test
  void shouldHandleDeclinedPayment() throws Exception {
    PostPaymentRequest request = createValidRequest();

    mockBankServer.expect(requestTo(bankUrl))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withSuccess(BANK_UNAUTHORIZED_RESPONSE_JSON, MediaType.APPLICATION_JSON));

    client.post("/api/payments", request)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value(PaymentStatus.DECLINED.getName()));

    mockBankServer.verify();
  }

  @Test
  void shouldHandleBankServerError() throws Exception {
    PostPaymentRequest request = createValidRequest();

    mockBankServer.expect(requestTo(bankUrl))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

    client.post("/api/payments", request)
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message").value("An internal error occurred"));

    mockBankServer.verify();
  }
}
