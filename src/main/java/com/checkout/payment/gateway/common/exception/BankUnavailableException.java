package com.checkout.payment.gateway.common.exception;

public class BankUnavailableException extends RetryableBankException {
    public BankUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
