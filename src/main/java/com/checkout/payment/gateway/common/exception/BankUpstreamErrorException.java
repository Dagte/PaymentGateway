package com.checkout.payment.gateway.common.exception;

public class BankUpstreamErrorException extends RuntimeException {
    public BankUpstreamErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
