package com.trendyol.jdempotent.core.chain;

import com.trendyol.jdempotent.core.annotation.JdempotentProperty;
import com.trendyol.jdempotent.core.model.ChainData;
import com.trendyol.jdempotent.core.model.KeyValuePair;

import java.lang.reflect.Field;

public class JdempotentPropertyAnnotationChain extends AnnotationChain {

    @Override
    public KeyValuePair process(ChainData chainData) throws IllegalAccessException {
        Field declaredField = chainData.getDeclaredField();
        declaredField.setAccessible(true);
        JdempotentProperty annotation = declaredField.getAnnotation(JdempotentProperty.class);
        if(annotation != null){
            return new KeyValuePair(annotation.value(),declaredField.get(chainData.getArgs()));
        }
        return super.nextChain.process(chainData);
    }
}
