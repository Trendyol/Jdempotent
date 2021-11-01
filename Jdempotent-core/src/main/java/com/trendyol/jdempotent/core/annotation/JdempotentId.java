package com.trendyol.jdempotent.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * Places the generated idempotency identifier into annotated field.
 * <pre>{@code
 *
 * public class JavadocTest {
 *
 *  @JdempotentId
 *  private String jdempotentId;
 *  private Object data;
 *
 * }
 * }</pre>
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JdempotentId {
}