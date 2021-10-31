package com.trendyol.jdempotent.core.model;

import java.io.Serializable;

/**
 *
 * Wraps the combine of application name, listener name and incoming event value hash
 *
 */
public class IdempotencyKeyHeader implements Serializable {

    private String keyHeaderValue;

    public IdempotencyKeyHeader() {
    }

    public IdempotencyKeyHeader(String keyValue) {
        this.keyHeaderValue = keyValue;
    }

    public String getKeyValue() {
        return keyHeaderValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyHeaderValue = keyValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime + keyHeaderValue.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof IdempotencyKeyHeader)) {
            return false;
        }
        IdempotencyKeyHeader other = (IdempotencyKeyHeader) obj;
        if (keyHeaderValue == null) {
            if (other.keyHeaderValue != null) {
                return false;
            }
        } else if (!keyHeaderValue.equals(other.keyHeaderValue)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("IdempotencyKeyHeader [keyHeaderValue=%s]", keyHeaderValue);
    }
}
