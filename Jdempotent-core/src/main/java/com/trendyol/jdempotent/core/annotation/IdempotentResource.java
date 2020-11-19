package com.trendyol.jdempotent.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Add to the methods that need to be idempotent.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface IdempotentResource {

    /**
     * Prefix value to make hash value more collider
     *
     * @return
     */
    String cachePrefix() default "";
}