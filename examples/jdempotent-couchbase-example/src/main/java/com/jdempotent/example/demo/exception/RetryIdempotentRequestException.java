package com.jdempotent.example.demo.exception;

import javax.mail.MessagingException;

public class RetryIdempotentRequestException extends RuntimeException {
    public RetryIdempotentRequestException(MessagingException e) {
    }
}
