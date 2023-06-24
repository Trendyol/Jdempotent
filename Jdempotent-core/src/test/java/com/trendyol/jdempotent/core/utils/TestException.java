package com.trendyol.jdempotent.core.utils;

public class TestException extends RuntimeException {
    public TestException(){
        super();
    }

    public TestException(String message){
        super(message);
    }
}
