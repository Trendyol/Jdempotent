package com.Jdempotent.core.generator;

import com.Jdempotent.core.annotation.IdempotentResource;
import com.Jdempotent.core.constant.EnvironmentVariableUtils;
import com.Jdempotent.core.model.IdempotencyKey;
import com.Jdempotent.core.model.IdempotentRequestWrapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

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

        if (!StringUtils.isEmpty(appName)) {
            builder.append(appName);
            builder.append("-");
        }

        if (!StringUtils.isEmpty(listenerName)) {
            builder.append(listenerName);
            builder.append("-");
        }

        for (byte b : digest) {
            builder.append(Integer.toHexString(0xFF & b));
        }

        return new IdempotencyKey(builder.toString());
    }

}
