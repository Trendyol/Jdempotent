package com.trendyol.jdempotent.core.chain;

import com.trendyol.jdempotent.core.model.ChainData;
import com.trendyol.jdempotent.core.model.KeyValuePair;

import java.lang.reflect.Field;

public class JdempotentDefaultChain extends AnnotationChain {

    @Override
    public KeyValuePair process(ChainData chainData) throws IllegalAccessException {
        Field declaredField = chainData.getDeclaredField();
        declaredField.setAccessible(true);
        return new KeyValuePair(declaredField.getName(),declaredField.get(chainData.getArgs()));
    }
}
