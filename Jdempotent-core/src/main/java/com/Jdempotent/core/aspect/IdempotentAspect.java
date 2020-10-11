package com.Jdempotent.core.aspect;

import com.Jdempotent.core.annotation.IdempotentRequestPayload;
import com.Jdempotent.core.annotation.IdempotentResource;
import com.Jdempotent.core.callback.ErrorConditionalCallback;
import com.Jdempotent.core.constant.CryptographyAlgorithm;
import com.Jdempotent.core.constant.EnvironmentVariableUtils;
import com.Jdempotent.core.datasource.IdempotentRepository;
import com.Jdempotent.core.datasource.InMemoryIdempotentRepository;
import com.Jdempotent.core.model.IdempotencyKey;
import com.Jdempotent.core.model.IdempotentRequestWrapper;
import com.Jdempotent.core.model.IdempotentResponseWrapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 *
 * An aspect that used along with the @IdempotentResource annotation
 *
 */
@Aspect
public class IdempotentAspect {
    private static final Logger logger = LoggerFactory.getLogger(IdempotentAspect.class);
    private final String appName;
    private IdempotentRepository idempotentRepository;
    private ErrorConditionalCallback errorCallback;

    private static final ThreadLocal<MessageDigest> messageDigests =
            new ThreadLocal<>() {
                @Override
                protected MessageDigest initialValue() {
                    try {
                        return MessageDigest.getInstance(CryptographyAlgorithm.MD5.value());
                    } catch (NoSuchAlgorithmException e) {
                        logger.warn("This algorithm not supported.", e);
                    }
                    return null;
                }

                @Override
                public MessageDigest get() {
                    MessageDigest messageDigest = super.get();
                    messageDigest.reset();
                    return messageDigest;
                }
            };

    private static final ThreadLocal<StringBuilder> stringBuilders =
            new ThreadLocal<>() {
                @Override
                protected StringBuilder initialValue() {
                    return new StringBuilder();
                }

                @Override
                public StringBuilder get() {
                    StringBuilder builder = super.get();
                    builder.setLength(0);
                    return builder;
                }
            };

    public IdempotentAspect() {
        appName = System.getenv(EnvironmentVariableUtils.APP_NAME);
        idempotentRepository = new InMemoryIdempotentRepository();
    }

    public IdempotentAspect(ErrorConditionalCallback errorCallback) {
        this.errorCallback = errorCallback;
        appName = System.getenv(EnvironmentVariableUtils.APP_NAME);
        idempotentRepository = new InMemoryIdempotentRepository();
    }

    public IdempotentAspect(IdempotentRepository idempotentRepository) {
        appName = System.getenv(EnvironmentVariableUtils.APP_NAME);
        this.idempotentRepository = idempotentRepository;
    }

    public IdempotentAspect(IdempotentRepository idempotentRepository, ErrorConditionalCallback errorCallback) {
        appName = System.getenv(EnvironmentVariableUtils.APP_NAME);
        this.idempotentRepository = idempotentRepository;
        this.errorCallback = errorCallback;
    }

    /**
     * An advice to make sure it returns at the same time for all subsequent calls
     *
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("@annotation(com.Jdempotent.core.annotation.IdempotentResource)")
    public Object execute(ProceedingJoinPoint pjp) throws Throwable {
        String classAndMethodName = generateLogPrefixForIncomingEvent(pjp);
        IdempotentRequestWrapper requestObject = findIdempotentRequestArg(pjp);
        IdempotencyKey idempotencyKey = generateIdempotentKey(requestObject, pjp);

        logger.debug(classAndMethodName + "starting for {}", requestObject);

        if (idempotentRepository.contains(idempotencyKey)) {
            Object response = retrieveResponse(idempotencyKey);
            logger.debug(classAndMethodName + "ended up reading from cache for {}", requestObject);
            return response;
        }

        logger.debug(classAndMethodName + "saved to cache with {}", idempotencyKey);
        idempotentRepository.store(idempotencyKey, requestObject);
        Object result;
        try {
            result = pjp.proceed();
        } catch (Exception e) {
            logger.debug(classAndMethodName + "deleted from cache with {} . Exception : {}", idempotencyKey,e);
            idempotentRepository.remove(idempotencyKey);
            throw e;
        }

        if (errorCallback != null && errorCallback.onErrorCondition(result)) {
            idempotentRepository.remove(idempotencyKey);
            throw errorCallback.onErrorCustomException();
        }

        idempotentRepository.setResponse(idempotencyKey, requestObject, new IdempotentResponseWrapper(result));
        logger.debug(classAndMethodName + "ended for {}", requestObject);
        return result;
    }

    /**
     * Generates log prefix for the incoming event
     *
     * @param pjp
     * @return
     */
    private String generateLogPrefixForIncomingEvent(ProceedingJoinPoint pjp) {
        StringBuilder builder = stringBuilders.get();
        String className = pjp.getTarget().getClass().getSimpleName();
        String methodName = pjp.getSignature().getName();
        builder.append(className);
        builder.append(".");
        builder.append(methodName);
        builder.append("() ");
        return builder.toString();
    }

    /**
     *
     * Retrieve response from cache
     *
     * @param key
     * @return
     */
    private Object retrieveResponse(IdempotencyKey key) {
        IdempotentResponseWrapper response = idempotentRepository.getResponse(key);
        if (response != null) {
            return response.getResponse();
        }
        return null;
    }

    /**
     * Finds the idempotent object
     *
     * @param pjp
     * @return
     */
    private IdempotentRequestWrapper findIdempotentRequestArg(ProceedingJoinPoint pjp) {
        Object[] args = pjp.getArgs();
        if (args.length == 0) {
            throw new IllegalStateException("Idempotent method not found");
        } else if (args.length == 1) {
            return new IdempotentRequestWrapper(args[0]);
        } else {
            try {
                MethodSignature signature = (MethodSignature) pjp.getSignature();
                String methodName = signature.getMethod().getName();
                Class<?>[] parameterTypes = signature.getMethod().getParameterTypes();
                Annotation[][] annotations = pjp.getTarget().getClass().getMethod(methodName, parameterTypes)
                        .getParameterAnnotations();
                for (int i = 0; i < args.length; i++) {
                    for (Annotation annotation : annotations[i]) {
                        if (annotation instanceof IdempotentRequestPayload) {
                            return new IdempotentRequestWrapper(args[i]);
                        }
                    }
                }
            } catch (NoSuchMethodException | SecurityException e) {
                throw new IllegalStateException("Idempotent method not found", e);
            }
        }
        throw new IllegalStateException("Idempotent method not found");
    }


    /**
     * Sets the cache implementation
     *
     * @param idempotentRepository
     */
    public void setIdempotentRepository(IdempotentRepository idempotentRepository) {
        this.idempotentRepository = idempotentRepository;
    }

    /**
     *
     * Generates a idempotent key for incoming event
     *
     * @param requestObject
     * @param pjp
     * @return
     */
    private IdempotencyKey generateIdempotentKey(IdempotentRequestWrapper requestObject, ProceedingJoinPoint pjp) {
        StringBuilder builder = stringBuilders.get();
        MessageDigest messageDigest = messageDigests.get();
        String listenerName = ((MethodSignature) pjp.getSignature()).getMethod().getAnnotation(IdempotentResource.class).cachePrefix();

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