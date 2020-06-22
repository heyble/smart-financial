package com.smart.financial.common;

public class SmartException extends Exception {

    public SmartException(String message) {
        super(message);
    }

    public SmartException(String message, Throwable cause) {
        super(message, cause);
    }
}
