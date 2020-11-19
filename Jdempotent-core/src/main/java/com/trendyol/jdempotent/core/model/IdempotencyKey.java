package com.trendyol.jdempotent.core.model;

import java.io.Serializable;

/**
 *
 * Wraps the combine of application name, listener name and incoming event value hash
 *
 */
public class IdempotencyKey implements Serializable {

    private String keyValue;

    public IdempotencyKey() {
    }

    public IdempotencyKey(String keyValue) {
        this.keyValue = keyValue;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime + keyValue.hashCode();
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
        if (!(obj instanceof IdempotencyKey)) {
            return false;
        }
        IdempotencyKey other = (IdempotencyKey) obj;
        if (keyValue == null) {
            if (other.keyValue != null) {
                return false;
            }
        } else if (!keyValue.equals(other.keyValue)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("IdempotencyKey [keyValue=%s]", keyValue);
    }
}
