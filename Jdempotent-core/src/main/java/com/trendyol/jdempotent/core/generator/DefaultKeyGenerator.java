package com.trendyol.jdempotent.core.generator;

import com.trendyol.jdempotent.core.constant.EnvironmentVariableUtils;
import com.trendyol.jdempotent.core.model.IdempotencyKey;
import com.trendyol.jdempotent.core.model.IdempotentRequestWrapper;
import java.io.UnsupportedEncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 *
 *
 */
public class DefaultKeyGenerator implements KeyGenerator {

    private static final Logger logger = LoggerFactory.getLogger(DefaultKeyGenerator.class);
    private final String appName;
    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

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
    @Override
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

    /**
     *
     * Generates a idempotent key for incoming header event  with the v4 UUID format 
     *
     * @param idempotentHeaderKey
     * @return
     */
    @Override
    public IdempotencyKey generateIdempotentKey(String idempotentHeaderKey) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        if (StringUtils.isEmpty(idempotentHeaderKey)) {
            MessageDigest salt = MessageDigest.getInstance("SHA-256");
            salt.update(UUID.randomUUID()
                    .toString()
                    .getBytes("UTF-8"));
            idempotentHeaderKey = bytesToHex(salt.digest());
        }        
        return new IdempotencyKey(idempotentHeaderKey);
    }
    
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
