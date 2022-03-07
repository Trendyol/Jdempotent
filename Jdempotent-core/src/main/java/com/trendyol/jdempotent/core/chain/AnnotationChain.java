package com.trendyol.jdempotent.core.chain;

import com.trendyol.jdempotent.core.model.ChainData;
import com.trendyol.jdempotent.core.model.KeyValuePair;

public abstract class AnnotationChain {
    protected AnnotationChain nextChain;

    public abstract KeyValuePair process(ChainData chainData) throws IllegalAccessException;

    public void next(AnnotationChain nextChain) {
        this.nextChain = nextChain;
    }
}
