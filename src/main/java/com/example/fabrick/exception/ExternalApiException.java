package com.example.fabrick.exception;

public class ExternalApiException  extends RuntimeException {
    public ExternalApiException(String message) { super(message); }
    public ExternalApiException(String message, Throwable t) { super(message, t); }
}
