package com.trendyol.jdempotent.core.model;

import java.io.Serializable;

/**
 *
 *  That is a container for idempotent requests and responses
 *
 */
@SuppressWarnings("serial")
public class IdempotentRequestResponseWrapper implements Serializable {
    private IdempotentRequestWrapper request;
    private IdempotentResponseWrapper response = null;

    public IdempotentRequestResponseWrapper(){}

    public IdempotentRequestResponseWrapper(IdempotentRequestWrapper request) {
        this.request = request;
    }

    public IdempotentResponseWrapper getResponse() {
        return response;
    }

    public void setResponse(IdempotentResponseWrapper response) {
        synchronized (this) {
            this.response = response;
        }
    }

    public IdempotentRequestWrapper getRequest() {
        return request;
    }

    @Override
    public String toString() {
        return String.format("IdempotentRequestResponseWrapper [request=%s, response=%s]", request, response);
    }
}
