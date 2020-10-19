package com.Jdempotent.core.generator;

import com.Jdempotent.core.model.IdempotencyKey;
import com.Jdempotent.core.model.IdempotentRequestWrapper;

import java.security.MessageDigest;

/**
 *
 */
public interface KeyGenerator {

    /**
     *
     * @param requestObject
     * @param listenerName
     * @param builder
     * @param messageDigest
     * @return
     */
    IdempotencyKey generateIdempotentKey(IdempotentRequestWrapper requestObject, String listenerName, StringBuilder builder, MessageDigest messageDigest);

}
