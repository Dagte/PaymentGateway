package com.checkout.payment.gateway.common.exception;

public class BankTimeoutException extends RetryableBankException {
    public BankTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
