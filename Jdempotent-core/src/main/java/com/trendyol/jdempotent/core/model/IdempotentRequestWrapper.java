package com.trendyol.jdempotent.core.model;

import java.io.Serializable;

/**
 *
 * Wraps the incoming event value
 *
 */
@SuppressWarnings("serial")
public class IdempotentRequestWrapper implements Serializable {
    private final Object request;

    public IdempotentRequestWrapper(Object request) {
        this.request = request;
    }

    public Object getRequest() {
        return request;
    }

    @Override
    public int hashCode() {
        return request == null ? 0 : request.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return request == null ? false : request.equals(obj);
    }

    @Override
    public String toString() {
        return String.format("IdempotentRequestWrapper [request=%s]", request);
    }
}
