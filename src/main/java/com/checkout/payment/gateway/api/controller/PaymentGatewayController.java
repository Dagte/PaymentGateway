package com.checkout.payment.gateway.api.controller;

import com.checkout.payment.gateway.api.dto.GetPaymentResponse;
import com.checkout.payment.gateway.api.dto.PostPaymentRequest;
import com.checkout.payment.gateway.api.dto.PostPaymentResponse;
import com.checkout.payment.gateway.api.mapper.PaymentMapper;
import com.checkout.payment.gateway.core.model.Payment;
import com.checkout.payment.gateway.core.service.PaymentGatewayService;
import com.checkout.payment.gateway.core.service.PaymentProcessResult;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PaymentGatewayController {

  private final PaymentGatewayService paymentGatewayService;
  private final PaymentMapper paymentMapper;

  public PaymentGatewayController(PaymentGatewayService paymentGatewayService,
      PaymentMapper paymentMapper) {
    this.paymentGatewayService = paymentGatewayService;
    this.paymentMapper = paymentMapper;
  }

  @GetMapping("/payments/{id}")
  public ResponseEntity<GetPaymentResponse> getPostPaymentEventById(@PathVariable UUID id) {
    Payment payment = paymentGatewayService.getPaymentById(id);
    return new ResponseEntity<>(paymentMapper.toGetPaymentResponse(payment), HttpStatus.OK);
  }

  @PostMapping("/payments")
  public ResponseEntity<PostPaymentResponse> processPayment(
      @Valid @RequestBody PostPaymentRequest paymentRequest,
      @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {

    Payment payment = paymentMapper.toDomain(paymentRequest);
    PaymentProcessResult result = paymentGatewayService.processPayment(payment, paymentRequest.getCardNumber(),
        paymentRequest.getCvv(), idempotencyKey);
    
    PostPaymentResponse response = paymentMapper.toPostPaymentResponse(result.payment());
    return new ResponseEntity<>(response, getResponseStatus(result));
  }

  @NonNull
  private static HttpStatus getResponseStatus(PaymentProcessResult result) {
    return result.isRetry() ? HttpStatus.OK : HttpStatus.CREATED;
  }

}
