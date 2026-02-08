package com.checkout.payment.gateway.api.mapper;

import com.checkout.payment.gateway.api.dto.GetPaymentResponse;
import com.checkout.payment.gateway.api.dto.PostPaymentRequest;
import com.checkout.payment.gateway.api.dto.PostPaymentResponse;
import com.checkout.payment.gateway.core.model.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

  public Payment toDomain(PostPaymentRequest request) {
    Payment payment = new Payment();
    payment.setExpiryMonth(request.getExpiryMonth());
    payment.setExpiryYear(request.getExpiryYear());
    payment.setCurrency(request.getCurrency());
    payment.setAmount(request.getAmount());
    
    if (request.getCardNumber() != null && request.getCardNumber().length() >= 4) {
      payment.setCardNumberLastFour(request.getCardNumber().substring(request.getCardNumber().length() - 4));
    }
    
    return payment;
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
