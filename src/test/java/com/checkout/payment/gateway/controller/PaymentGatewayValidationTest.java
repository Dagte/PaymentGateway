package com.checkout.payment.gateway.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.service.PaymentGatewayService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

class PaymentGatewayValidationTest extends BasePaymentGatewayTest {

  @MockBean
  private PaymentGatewayService paymentGatewayService;

  @Test
  void whenCardNumberIsTooShortThenReturnBadRequest() throws Exception {
    PostPaymentRequest request = createValidRequest();
    request.setCardNumber("1234567890123");

    client.post("/api/payments", request)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("size must be between 14 and 19"));
  }

  @Test
  void whenCardNumberContainsAlphaThenReturnBadRequest() throws Exception {
    PostPaymentRequest request = createValidRequest();
    request.setCardNumber("12345678901234A");

    client.post("/api/payments", request)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Card number must only contain numeric characters"));
  }

  @Test
  void whenCurrencyIsInvalidThenReturnBadRequest() throws Exception {
    PostPaymentRequest request = createValidRequest();
    request.setCurrency("JPY");

    client.post("/api/payments", request)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Currency must be one of: USD, GBP, EUR"));
  }

  @Test
  void whenCvvIsTooLongThenReturnBadRequest() throws Exception {
    PostPaymentRequest request = createValidRequest();
    request.setCvv("12345");

    client.post("/api/payments", request)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("size must be between 3 and 4"));
  }

  @Test
  void whenExpiryDateIsInThePastThenReturnBadRequest() throws Exception {
    PostPaymentRequest request = createValidRequest();
    request.setExpiryMonth(1);
    request.setExpiryYear(2020);

    client.post("/api/payments", request)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("The card expiry date must be in the future"));
  }

  @Test
  void whenMultipleErrorsHappenThenReturnBadRequestWithBothErrors() throws Exception {
    PostPaymentRequest request = createValidRequest();
    request.setExpiryMonth(1);
    request.setExpiryYear(2020);
    request.setCurrency("JPY");

    client.post("/api/payments", request)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(containsString("The card expiry date must be in the future")))
        .andExpect(jsonPath("$.message").value(containsString("Currency must be one of: USD, GBP, EUR")));
  }

  @Test
  void whenJsonIsMalformedThenReturnBadRequest() throws Exception {
    String malformedJson = "{ \"card_number\": \"1234567890123456\", \"cvv\": \"123\" ";

    client.postRaw("/api/payments", malformedJson)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Malformed JSON request"));
  }

  @Test
  void whenUnexpectedExceptionOccursThenReturnInternalServerError() throws Exception {
    PostPaymentRequest request = createValidRequest();
    when(paymentGatewayService.processPayment(any())).thenThrow(new RuntimeException("Simulated internal error"));

    client.post("/api/payments", request)
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message").value("An internal error occurred"));
  }
}
