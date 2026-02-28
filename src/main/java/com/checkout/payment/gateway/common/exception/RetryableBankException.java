package com.checkout.payment.gateway.common.exception;

public abstract class RetryableBankException extends RuntimeException {
    protected RetryableBankException(String message, Throwable cause) {
        super(message, cause);
    }
}
