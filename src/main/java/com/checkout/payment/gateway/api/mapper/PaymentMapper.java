package com.checkout.payment.gateway.api.mapper;

import com.checkout.payment.gateway.api.dto.GetPaymentResponse;
import com.checkout.payment.gateway.api.dto.PostPaymentRequest;
import com.checkout.payment.gateway.api.dto.PostPaymentResponse;
import com.checkout.payment.gateway.core.model.Payment;
import com.checkout.payment.gateway.infrastructure.client.bank.model.BankPaymentRequest;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

  public Payment toDomain(PostPaymentRequest request) {
    Payment payment = new Payment();
    payment.setCardNumber(request.getCardNumber());
    payment.setExpiryMonth(request.getExpiryMonth());
    payment.setExpiryYear(request.getExpiryYear());
    payment.setCurrency(request.getCurrency());
    payment.setAmount(request.getAmount());
    payment.setCvv(request.getCvv());
    return payment;
  }

  public BankPaymentRequest toBankRequest(Payment domain) {
    String expiryDate = String.format("%02d/%d", domain.getExpiryMonth(), domain.getExpiryYear());
    return new BankPaymentRequest(
        domain.getCardNumber(),
        expiryDate,
        domain.getCurrency(),
        domain.getAmount(),
        domain.getCvv()
    );
  }

  public PostPaymentResponse toPostPaymentResponse(Payment domain) {
    PostPaymentResponse response = new PostPaymentResponse();
    response.setId(domain.getId());
    response.setStatus(domain.getStatus());
    response.setCardNumberLastFour(domain.getCardNumberLastFour());
    response.setExpiryMonth(domain.getExpiryMonth());
    response.setExpiryYear(domain.getExpiryYear());
    response.setCurrency(domain.getCurrency());
    response.setAmount(domain.getAmount());
    return response;
  }

  public GetPaymentResponse toGetPaymentResponse(Payment domain) {
    GetPaymentResponse response = new GetPaymentResponse();
    response.setId(domain.getId());
    response.setStatus(domain.getStatus());
    response.setCardNumberLastFour(domain.getCardNumberLastFour());
    response.setExpiryMonth(domain.getExpiryMonth());
    response.setExpiryYear(domain.getExpiryYear());
    response.setCurrency(domain.getCurrency());
    response.setAmount(domain.getAmount());
    return response;
  }
}
