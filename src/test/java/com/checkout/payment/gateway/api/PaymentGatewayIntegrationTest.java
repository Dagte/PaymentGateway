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
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import org.springframework.web.client.RestTemplate;

class PaymentGatewayIntegrationTest extends BasePaymentGatewayTest {

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
  void shouldHandleBankServiceUnavailable() throws Exception {
    PostPaymentRequest request = createValidRequest();

    mockBankServer.expect(ExpectedCount.between(1, 3), requestTo(bankUrl))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

    client.post("/api/payments", request)
        .andExpect(status().isServiceUnavailable())
        .andExpect(jsonPath("$.message").value("Acquiring bank is currently unavailable"));

    mockBankServer.verify();
  }

  @Test
  void shouldHandleBankTimeout() throws Exception {
    PostPaymentRequest request = createValidRequest();

    mockBankServer.expect(ExpectedCount.between(1, 3), requestTo(bankUrl))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withException(new java.net.SocketTimeoutException("Timeout")));

    client.post("/api/payments", request)
        .andExpect(status().isGatewayTimeout())
        .andExpect(jsonPath("$.message").value("Communication with acquiring bank timed out"));

    mockBankServer.verify();
  }

  @Test
  void shouldHandleBankInternalError() throws Exception {
    PostPaymentRequest request = createValidRequest();

    mockBankServer.expect(ExpectedCount.between(1, 3), requestTo(bankUrl))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

    client.post("/api/payments", request)
        .andExpect(status().isBadGateway())
        .andExpect(jsonPath("$.message").value("Acquiring bank returned an internal error"));

    mockBankServer.verify();
  }
}
