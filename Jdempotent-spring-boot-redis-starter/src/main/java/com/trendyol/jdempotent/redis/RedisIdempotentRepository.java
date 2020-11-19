package com.trendyol.jdempotent.redis;

/**
 * import com.trendyol.jdempotent.core.datasource.AbstractIdempotentRepository;
 * import com.trendyol.jdempotent.core.model.IdempotencyKey;
 * import com.trendyol.jdempotent.core.model.IdempotentRequestResponseWrapper;
 * <p>
 * import java.util.Map;
 * <p>
 * /**
 * <p>
 * An implementation of the idempotent IdempotentRepository
 * that uses a distributed hash map from Redis
 */

import com.trendyol.jdempotent.core.datasource.IdempotentRepository;
import com.trendyol.jdempotent.core.model.IdempotencyKey;
import com.trendyol.jdempotent.core.model.IdempotentRequestResponseWrapper;
import com.trendyol.jdempotent.core.model.IdempotentRequestWrapper;
import com.trendyol.jdempotent.core.model.IdempotentResponseWrapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

/**
 *
 */
public class RedisIdempotentRepository implements IdempotentRepository {

    private final ValueOperations<IdempotencyKey, IdempotentRequestResponseWrapper> valueOperations;
    private final RedisTemplate redisTemplate;
    private final RedisConfigProperties redisProperties;


    public RedisIdempotentRepository(RedisTemplate redisTemplate, RedisConfigProperties redisProperties) {
        valueOperations = redisTemplate.opsForValue();
        this.redisTemplate = redisTemplate;
        this.redisProperties = redisProperties;
    }

    @Override
    public boolean contains(IdempotencyKey idempotencyKey) {
        return valueOperations.get(idempotencyKey) != null;
    }

    @Override
    public IdempotentResponseWrapper getResponse(IdempotencyKey idempotencyKey) {
        return valueOperations.get(idempotencyKey).getResponse();
    }

    @Override
    public void store(IdempotencyKey idempotencyKey, IdempotentRequestWrapper request) {
        valueOperations.set(idempotencyKey, new IdempotentRequestResponseWrapper(request), redisProperties.getExpirationTimeHour(), TimeUnit.HOURS);
    }

    @Override
    public void remove(IdempotencyKey idempotencyKey) {
        redisTemplate.delete(idempotencyKey);
    }

    @Override
    public void setResponse(IdempotencyKey idempotencyKey, IdempotentRequestWrapper request, IdempotentResponseWrapper response) {
        if (contains(idempotencyKey)) {
            IdempotentRequestResponseWrapper requestResponseWrapper = valueOperations.get(idempotencyKey);
            requestResponseWrapper.setResponse(response);
            valueOperations.set(idempotencyKey, new IdempotentRequestResponseWrapper(request), redisProperties.getExpirationTimeHour(), TimeUnit.HOURS);
        }
    }

}
