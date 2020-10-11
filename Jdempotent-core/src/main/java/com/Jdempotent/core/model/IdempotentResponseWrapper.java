package com.Jdempotent.core.model;

import java.io.Serializable;

/**
 * Wraps the incoming event response
 *
 */
@SuppressWarnings("serial")
public class IdempotentResponseWrapper implements Serializable {

    private final Object response;

    public IdempotentResponseWrapper(Object response) {
        this.response = response;
    }

    public Object getResponse() {
        return response;
    }

    @Override
    public int hashCode() {
        return response == null ? 0 : response.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return response == null ? false : response.equals(obj);
    }

    @Override
    public String toString() {
        return String.format("IdempotentResponseWrapper [response=%s]", response);
    }
}
