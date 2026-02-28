package com.checkout.payment.gateway.api.validation;

import com.checkout.payment.gateway.api.dto.PostPaymentRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.YearMonth;

public class PaymentExpiryValidator implements ConstraintValidator<ValidPaymentExpiry, PostPaymentRequest> {

  @Override
  public boolean isValid(PostPaymentRequest request, ConstraintValidatorContext context) {
    if (request == null) {
      return true;
    }

    try {
      YearMonth expiryDate = YearMonth.of(request.getExpiryYear(), request.getExpiryMonth());
      YearMonth currentMonth = YearMonth.now();

      return expiryDate.isAfter(currentMonth) || expiryDate.equals(currentMonth);
    } catch (Exception e) {
      return false;
    }
  }
}