package com.trendyol.jdempotent.core.model;

import org.springframework.util.comparator.Comparators;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * Wraps the incoming event value
 *
 */
@SuppressWarnings("serial")
public class IdempotentRequestWrapper implements Serializable {
    private List<Object> request;

    public IdempotentRequestWrapper(){
    }

    public IdempotentRequestWrapper(Object request) {
        this.request = Collections.singletonList(request);
    }

    public IdempotentRequestWrapper(List<Object> request) {
        this.request = request;
    }

    public List<Object> getRequest() {
        return request;
    }

    @Override
    public int hashCode() {
        return request == null ? 0 : request.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return !Objects.isNull(request) && request.stream().anyMatch(req -> req.equals(obj));
    }

    @Override
    public String toString() {
        StringBuilder requestBuilder = new StringBuilder();
        this.request.stream()
                .map(Object::toString)
                .collect(Collectors.toList()).stream()
                .sorted(String::compareTo)
                .forEach(requestBuilder::append);
        System.out.println(requestBuilder.toString());
        return String.format("IdempotentRequestWrapper [request=%s]", requestBuilder.toString());
    }
}
