package com.checkout.payment.gateway.core.service;

import com.checkout.payment.gateway.core.model.Payment;

public record PaymentProcessResult(Payment payment, boolean isRetry) {}
