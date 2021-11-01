package com.trendyol.jdempotent.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Add to the methods arguments that represents the idempotent request payload.
 *
 */
@Deprecated
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface IdempotentRequestPayload {
}