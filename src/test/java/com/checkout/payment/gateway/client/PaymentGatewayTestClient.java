package com.checkout.payment.gateway.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class PaymentGatewayTestClient {

  private final MockMvc mvc;
  private final ObjectMapper objectMapper;

  public PaymentGatewayTestClient(MockMvc mvc, ObjectMapper objectMapper) {
    this.mvc = mvc;
    this.objectMapper = objectMapper;
  }

  public ResultActions post(String url, Object requestBody) throws Exception {
    return mvc.perform(MockMvcRequestBuilders.post(url)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestBody)));
  }

  public ResultActions postRaw(String url, String rawBody) throws Exception {
    return mvc.perform(MockMvcRequestBuilders.post(url)
        .contentType(MediaType.APPLICATION_JSON)
        .content(rawBody));
  }

  public ResultActions get(String url) throws Exception {
    return mvc.perform(MockMvcRequestBuilders.get(url));
  }
}