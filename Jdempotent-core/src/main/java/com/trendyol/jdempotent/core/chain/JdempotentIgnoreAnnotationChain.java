package com.trendyol.jdempotent.core.chain;

import com.trendyol.jdempotent.core.annotation.JdempotentIgnore;
import com.trendyol.jdempotent.core.model.ChainData;
import com.trendyol.jdempotent.core.model.KeyValuePair;

import java.lang.reflect.Field;

public class JdempotentIgnoreAnnotationChain extends AnnotationChain {
    @Override
    public KeyValuePair process(ChainData chainData) throws IllegalAccessException {
        Field declaredField = chainData.getDeclaredField();
        declaredField.setAccessible(true);
        JdempotentIgnore annotation = declaredField.getAnnotation(JdempotentIgnore.class);
        if(annotation != null){
            return new KeyValuePair();
        }
        return super.nextChain.process(chainData);
    }
}
