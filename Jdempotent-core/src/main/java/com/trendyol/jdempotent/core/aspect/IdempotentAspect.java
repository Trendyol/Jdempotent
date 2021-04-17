package com.trendyol.jdempotent.core.aspect;

import com.trendyol.jdempotent.core.annotation.IdempotentIgnore;
import com.trendyol.jdempotent.core.annotation.IdempotentRequestPayload;
import com.trendyol.jdempotent.core.annotation.IdempotentResource;
import com.trendyol.jdempotent.core.callback.ErrorConditionalCallback;
import com.trendyol.jdempotent.core.constant.CryptographyAlgorithm;
import com.trendyol.jdempotent.core.datasource.IdempotentRepository;
import com.trendyol.jdempotent.core.datasource.InMemoryIdempotentRepository;
import com.trendyol.jdempotent.core.generator.DefaultKeyGenerator;
import com.trendyol.jdempotent.core.generator.KeyGenerator;
import com.trendyol.jdempotent.core.model.IdempotencyKey;
import com.trendyol.jdempotent.core.model.IdempotentIgnorableWrapper;
import com.trendyol.jdempotent.core.model.IdempotentRequestWrapper;
import com.trendyol.jdempotent.core.model.IdempotentResponseWrapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;


/**
 * An aspect that used along with the @IdempotentResource annotation
 */
@Aspect
public class IdempotentAspect {
    private static final Logger logger = LoggerFactory.getLogger(IdempotentAspect.class);
    private KeyGenerator keyGenerator;
    private IdempotentRepository idempotentRepository;
    private ErrorConditionalCallback errorCallback;
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

    public IdempotentAspect() {
        this.idempotentRepository = new InMemoryIdempotentRepository();
        this.keyGenerator = new DefaultKeyGenerator();
    }

    public IdempotentAspect(ErrorConditionalCallback errorCallback) {
        this.errorCallback = errorCallback;
        this.idempotentRepository = new InMemoryIdempotentRepository();
        this.keyGenerator = new DefaultKeyGenerator();
    }

    public IdempotentAspect(IdempotentRepository idempotentRepository) {
        this.idempotentRepository = idempotentRepository;
        this.keyGenerator = new DefaultKeyGenerator();
    }

    public IdempotentAspect(IdempotentRepository idempotentRepository, ErrorConditionalCallback errorCallback) {
        this.idempotentRepository = idempotentRepository;
        this.errorCallback = errorCallback;
        this.keyGenerator = new DefaultKeyGenerator();
    }

    public IdempotentAspect(ErrorConditionalCallback errorCallback, DefaultKeyGenerator keyGenerator) {
        this.errorCallback = errorCallback;
        this.idempotentRepository = new InMemoryIdempotentRepository();
        this.keyGenerator = keyGenerator;
    }

    public IdempotentAspect(IdempotentRepository idempotentRepository, DefaultKeyGenerator keyGenerator) {
        this.idempotentRepository = idempotentRepository;
        this.keyGenerator = keyGenerator;
    }

    public IdempotentAspect(IdempotentRepository idempotentRepository, ErrorConditionalCallback errorCallback, DefaultKeyGenerator keyGenerator) {
        this.idempotentRepository = idempotentRepository;
        this.errorCallback = errorCallback;
        this.keyGenerator = keyGenerator;
    }

    /**
     * An advice to make sure it returns at the same time for all subsequent calls
     *
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("@annotation(com.trendyol.jdempotent.core.annotation.IdempotentResource)")
    public Object execute(ProceedingJoinPoint pjp) throws Throwable {
        String classAndMethodName = generateLogPrefixForIncomingEvent(pjp);
        IdempotentRequestWrapper requestObject = findIdempotentRequestArg(pjp);
        String listenerName = ((MethodSignature) pjp.getSignature()).getMethod().getAnnotation(IdempotentResource.class).cachePrefix();
        IdempotencyKey idempotencyKey = keyGenerator.generateIdempotentKey(requestObject, listenerName, stringBuilders.get(), messageDigests.get());
        Long customTtl = ((MethodSignature) pjp.getSignature()).getMethod().getAnnotation(IdempotentResource.class).ttl();
        TimeUnit timeUnit = ((MethodSignature) pjp.getSignature()).getMethod().getAnnotation(IdempotentResource.class).ttlTimeUnit();

        logger.debug(classAndMethodName + "starting for {}", requestObject);

        if (idempotentRepository.contains(idempotencyKey)) {
            Object response = retrieveResponse(idempotencyKey);
            logger.debug(classAndMethodName + "ended up reading from cache for {}", requestObject);
            return response;
        }

        logger.debug(classAndMethodName + "saved to cache with {}", idempotencyKey);
        idempotentRepository.store(idempotencyKey, requestObject, customTtl, timeUnit);
        Object result;
        try {
            result = pjp.proceed();
        } catch (Exception e) {
            logger.debug(classAndMethodName + "deleted from cache with {} . Exception : {}", idempotencyKey, e);
            idempotentRepository.remove(idempotencyKey);
            throw e;
        }

        if (errorCallback != null && errorCallback.onErrorCondition(result)) {
            idempotentRepository.remove(idempotencyKey);
            throw errorCallback.onErrorCustomException();
        }


        idempotentRepository.setResponse(idempotencyKey, requestObject, new IdempotentResponseWrapper(result), customTtl, timeUnit);

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
    public IdempotentRequestWrapper findIdempotentRequestArg(ProceedingJoinPoint pjp) throws IllegalAccessException {
        Object[] args = pjp.getArgs();
        if (args.length == 0) {
            throw new IllegalStateException("Idempotent method not found");
        } else if (args.length == 1) {
            return new IdempotentRequestWrapper(getIdempotentNonIgnorableWrapper(args));
        } else {
            try {
                MethodSignature signature = (MethodSignature) pjp.getSignature();
                String methodName = signature.getMethod().getName();
                Class<?>[] parameterTypes = signature.getMethod().getParameterTypes();
                var method = pjp.getTarget().getClass().getMethod(methodName, parameterTypes);
                Annotation[][] annotations = method.getParameterAnnotations();
                for (int i = 0; i < args.length; i++) {
                    for (Annotation annotation : annotations[i]) {
                        if (annotation instanceof IdempotentRequestPayload) {
                            return new IdempotentRequestWrapper(getIdempotentNonIgnorableWrapper(args));
                        }
                    }
                }
            } catch (NoSuchMethodException | SecurityException e) {
                throw new IllegalStateException("Idempotent method not found", e);
            }
        }
        throw new IllegalStateException("Idempotent method not found");
    }

    public IdempotentIgnorableWrapper getIdempotentNonIgnorableWrapper(Object[] args) throws IllegalAccessException {
        var wrapper = new IdempotentIgnorableWrapper();
        Field[] declaredFields = args[0].getClass().getDeclaredFields();
        for (int i = 0; i < declaredFields.length; i++) {
            declaredFields[i].setAccessible(true);
            if (declaredFields[i].getDeclaredAnnotations().length == 0) {
                wrapper.getNonIgnoredFields().put(declaredFields[i].getName(), declaredFields[i].get(args[0]));
            } else {
                for (Annotation annotation : declaredFields[i].getDeclaredAnnotations()) {
                    if (!(annotation instanceof IdempotentIgnore)) {
                        wrapper.getNonIgnoredFields().put(declaredFields[i].getName(), declaredFields[i].get(args[0]));
                    }
                }
            }
        }
        return wrapper;
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
     * @return
     */
    public IdempotentRepository getIdempotentRepository() {
        return idempotentRepository;
    }
}