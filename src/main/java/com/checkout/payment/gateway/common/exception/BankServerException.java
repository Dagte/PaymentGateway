package com.checkout.payment.gateway.common.exception;

public class BankServerException extends RetryableBankException {
    public BankServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
