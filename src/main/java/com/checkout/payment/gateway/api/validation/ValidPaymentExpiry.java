package com.checkout.payment.gateway.api.validation;

import static com.checkout.payment.gateway.api.validation.ValidationErrorMessages.EXPIRY_DATE_PAST;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = PaymentExpiryValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPaymentExpiry {
  String message() default EXPIRY_DATE_PAST;
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}