package com.trendyol.jdempotent.core.generator;

import com.trendyol.jdempotent.core.constant.EnvironmentVariableUtils;
import com.trendyol.jdempotent.core.model.IdempotencyKey;
import com.trendyol.jdempotent.core.model.IdempotentRequestWrapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;

/**
 *
 *
 */
public class DefaultKeyGenerator implements KeyGenerator {

    private static final Logger logger = LoggerFactory.getLogger(DefaultKeyGenerator.class);
    private final String appName;

    public DefaultKeyGenerator() {
        appName = System.getenv(EnvironmentVariableUtils.APP_NAME);
    }

    /**
     *
     * Generates a idempotent key for incoming event
     *
     * @param requestObject
     * @param listenerName
     * @param builder
     * @param messageDigest
     * @return
     */
    public IdempotencyKey generateIdempotentKey(IdempotentRequestWrapper requestObject, String listenerName, StringBuilder builder, MessageDigest messageDigest) {
        messageDigest.update(requestObject.toString().getBytes());
        byte[] digest = messageDigest.digest();

        if (!StringUtils.isBlank(appName)) {
            builder.append(appName);
            builder.append("-");
        }

        if (!StringUtils.isBlank(listenerName)) {
            builder.append(listenerName);
            builder.append("-");
        }

        for (byte b : digest) {
            builder.append(Integer.toHexString(0xFF & b));
        }

        return new IdempotencyKey(builder.toString());
    }
}
