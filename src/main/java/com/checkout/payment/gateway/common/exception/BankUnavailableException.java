package com.checkout.payment.gateway.common.exception;

public class BankUnavailableException extends RuntimeException {
    public BankUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
