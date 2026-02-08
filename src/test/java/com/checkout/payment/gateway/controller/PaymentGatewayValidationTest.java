package com.checkout.payment.gateway.controller;

import static com.checkout.payment.gateway.validation.ValidationErrorMessages.CARD_NUMBER_INVALID_SIZE;
import static com.checkout.payment.gateway.validation.ValidationErrorMessages.CARD_NUMBER_NUMERIC;
import static com.checkout.payment.gateway.validation.ValidationErrorMessages.CURRENCY_INVALID;
import static com.checkout.payment.gateway.validation.ValidationErrorMessages.CVV_INVALID_SIZE;
import static com.checkout.payment.gateway.validation.ValidationErrorMessages.CVV_NUMERIC;
import static com.checkout.payment.gateway.validation.ValidationErrorMessages.EXPIRY_DATE_PAST;
import static com.checkout.payment.gateway.validation.ValidationErrorMessages.INTERNAL_ERROR;
import static com.checkout.payment.gateway.validation.ValidationErrorMessages.MALFORMED_JSON;
import static com.checkout.payment.gateway.validation.ValidationErrorMessages.VALIDATION_FAILED;
import static org.hamcrest.Matchers.hasItem;
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
        .andExpect(jsonPath("$.message").value(VALIDATION_FAILED))
        .andExpect(jsonPath("$.errors[?(@.field == 'cardNumber')].message").value(
            hasItem(CARD_NUMBER_INVALID_SIZE)));
  }

  @Test
  void whenCardNumberContainsAlphaThenReturnBadRequest() throws Exception {
    PostPaymentRequest request = createValidRequest();
    request.setCardNumber("12345678901234A");

    client.post("/api/payments", request)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(VALIDATION_FAILED))
        .andExpect(jsonPath("$.errors[?(@.field == 'cardNumber')].message").value(
            hasItem(CARD_NUMBER_NUMERIC)));
  }

  @Test
  void whenCurrencyIsInvalidThenReturnBadRequest() throws Exception {
    PostPaymentRequest request = createValidRequest();
    request.setCurrency("JPY");

    client.post("/api/payments", request)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(VALIDATION_FAILED))
        .andExpect(jsonPath("$.errors[?(@.field == 'currency')].message").value(
            hasItem(CURRENCY_INVALID)));
  }

  @Test
  void whenCvvIsTooLongThenReturnBadRequest() throws Exception {
    PostPaymentRequest request = createValidRequest();
    request.setCvv("12345");

    client.post("/api/payments", request)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(VALIDATION_FAILED))
        .andExpect(
            jsonPath("$.errors[?(@.field == 'cvv')].message").value(hasItem(CVV_INVALID_SIZE)));
  }

  @Test
  void whenCvvHasNotNumericCharactersThenReturnBadRequest() throws Exception {
    PostPaymentRequest request = createValidRequest();
    request.setCvv("12O");

    client.post("/api/payments", request)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(VALIDATION_FAILED))
        .andExpect(
            jsonPath("$.errors[?(@.field == 'cvv')].message").value(hasItem(CVV_NUMERIC)));
  }


  @Test
  void whenExpiryDateIsInThePastThenReturnBadRequest() throws Exception {
    PostPaymentRequest request = createValidRequest();
    request.setExpiryMonth(1);
    request.setExpiryYear(2020);

    client.post("/api/payments", request)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(VALIDATION_FAILED))
        .andExpect(jsonPath("$.errors[?(@.field == 'postPaymentRequest')].message").value(
            hasItem(EXPIRY_DATE_PAST)));
  }

  @Test
  void whenMultipleFieldsFailValidationThenReturnBadRequestWithMultipleErrors() throws Exception {
    PostPaymentRequest request = createValidRequest();
    request.setExpiryMonth(1);
    request.setExpiryYear(2020);
    request.setCurrency("JPY");

    client.post("/api/payments", request)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(VALIDATION_FAILED))
        .andExpect(jsonPath("$.errors[?(@.field == 'postPaymentRequest')].message").value(
            hasItem(EXPIRY_DATE_PAST)))
        .andExpect(jsonPath("$.errors[?(@.field == 'currency')].message").value(
            hasItem(CURRENCY_INVALID)));
  }

  @Test
  void whenJsonIsMalformedThenReturnBadRequest() throws Exception {
    String malformedJson = "{ \"card_number\": \"1234567890123456\", \"cvv\": \"123\" ";

    client.postRaw("/api/payments", malformedJson)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(MALFORMED_JSON));
  }

  @Test
  void whenUnexpectedExceptionOccursThenReturnInternalServerError() throws Exception {
    PostPaymentRequest request = createValidRequest();
    when(paymentGatewayService.processPayment(any())).thenThrow(
        new RuntimeException("Simulated internal error"));

    client.post("/api/payments", request)
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message").value(INTERNAL_ERROR));
  }
}