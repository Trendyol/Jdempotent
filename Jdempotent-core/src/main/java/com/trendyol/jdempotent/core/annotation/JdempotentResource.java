package com.trendyol.jdempotent.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

/**
 * Add to the methods that need to be idempotent.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface JdempotentResource {

    /**
     * Prefix value to make hash value more collider
     *
     * @return
     */
    String cachePrefix() default "";

    /**
     * To add custom ttl
     *
     * @return
     */
    long ttl() default 0L;

    /**
     * To add custom time unit
     *
     * @return
     */
    TimeUnit ttlTimeUnit() default TimeUnit.HOURS;
}