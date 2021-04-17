package com.trendyol.jdempotent.core.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class IdempotentIgnorableWrapper implements Serializable {
    private Map<String, Object> nonIgnoredFields;

    public IdempotentIgnorableWrapper() {
        nonIgnoredFields = new HashMap<>();
    }

    public Map<String, Object> getNonIgnoredFields() {
        return nonIgnoredFields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdempotentIgnorableWrapper wrapper = (IdempotentIgnorableWrapper) o;

        return Objects.equals(nonIgnoredFields, wrapper.nonIgnoredFields);
    }

    @Override
    public int hashCode() {
        return nonIgnoredFields != null ? nonIgnoredFields.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "IdempotentIgnorableWrapper{" +
                "nonIgnoredFields=" + nonIgnoredFields +
                '}';
    }
}
