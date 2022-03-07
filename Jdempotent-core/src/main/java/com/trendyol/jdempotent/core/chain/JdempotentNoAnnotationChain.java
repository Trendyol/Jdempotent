package com.trendyol.jdempotent.core.chain;

import com.trendyol.jdempotent.core.model.ChainData;
import com.trendyol.jdempotent.core.model.KeyValuePair;

import java.lang.reflect.Field;

public class JdempotentNoAnnotationChain extends AnnotationChain {

    @Override
    public KeyValuePair process(ChainData chainData) throws IllegalAccessException {
        if (chainData.getDeclaredField().getDeclaredAnnotations().length == 0) {
            Field declaredField = chainData.getDeclaredField();
            declaredField.setAccessible(true);
            return new KeyValuePair(declaredField.getName(),declaredField.get(chainData.getArgs()));
        }
        return super.nextChain.process(chainData);
    }
}
