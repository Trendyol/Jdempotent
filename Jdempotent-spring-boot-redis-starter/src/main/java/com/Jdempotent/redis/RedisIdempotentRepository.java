package com.Jdempotent.redis;

/**
 * import com.Jdempotent.core.datasource.AbstractIdempotentRepository;
 * import com.Jdempotent.core.model.IdempotencyKey;
 * import com.Jdempotent.core.model.IdempotentRequestResponseWrapper;
 * <p>
 * import java.util.Map;
 * <p>
 * /**
 * <p>
 * An implementation of the idempotent IdempotentRepository
 * that uses a distributed hash map from Redis
 */

import com.Jdempotent.core.constant.EnvironmentVariableUtils;
import com.Jdempotent.core.datasource.IdempotentRepository;
import com.Jdempotent.core.model.IdempotencyKey;
import com.Jdempotent.core.model.IdempotentRequestResponseWrapper;
import com.Jdempotent.core.model.IdempotentRequestWrapper;
import com.Jdempotent.core.model.IdempotentResponseWrapper;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisIdempotentRepository implements IdempotentRepository {

    private HashOperations hashOperations;
    private final String APPNAME;

    public RedisIdempotentRepository(RedisTemplate redisTemplate) {
        hashOperations = redisTemplate.opsForHash();
        APPNAME = System.getenv(EnvironmentVariableUtils.APP_NAME);
    }

    @Override
    public boolean contains(IdempotencyKey idempotencyKey) {
        return hashOperations.hasKey(APPNAME, idempotencyKey); //hashOperations.(mapName,IdempotencyKey);
    }

    @Override
    public IdempotentResponseWrapper getResponse(IdempotencyKey idempotencyKey) {
        return ((IdempotentRequestResponseWrapper) hashOperations.get(APPNAME, idempotencyKey)).getResponse();
    }

    @Override
    public void store(IdempotencyKey idempotencyKey, IdempotentRequestWrapper request) {
        hashOperations.put(APPNAME, idempotencyKey, new IdempotentRequestResponseWrapper(request));
    }

    @Override
    public void remove(IdempotencyKey idempotencyKey) {
        hashOperations.delete(APPNAME, idempotencyKey);
    }

    @Override
    public void setResponse(IdempotencyKey idempotencyKey, IdempotentRequestWrapper idempotentRequestWrapper, IdempotentResponseWrapper idempotentResponseWrapper) {
        if (contains(idempotencyKey)) {
            IdempotentRequestResponseWrapper requestResponseWrapper = (IdempotentRequestResponseWrapper) hashOperations.get(APPNAME, idempotencyKey);
            requestResponseWrapper.setResponse(idempotentResponseWrapper);
            hashOperations.put(APPNAME, idempotencyKey, requestResponseWrapper);
        }
    }

}
