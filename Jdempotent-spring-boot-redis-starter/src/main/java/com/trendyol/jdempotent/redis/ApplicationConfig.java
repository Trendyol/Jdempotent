package com.trendyol.jdempotent.redis;


import com.trendyol.jdempotent.core.aspect.IdempotentAspect;
import com.trendyol.jdempotent.core.callback.ErrorConditionalCallback;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 *
 */
@Configuration
@ConditionalOnProperty(
        value = "jdempotent.enable",
        havingValue = "true",
        matchIfMissing = true)
public class ApplicationConfig {

    private final RedisConfigProperties redisProperties;

    public ApplicationConfig(RedisConfigProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    @Bean
    @ConditionalOnProperty(
            value = "jdempotent.enable",
            havingValue = "true",
            matchIfMissing = true)
    @ConditionalOnClass(ErrorConditionalCallback.class)
    public IdempotentAspect getIdempotentAspect(RedisTemplate redisTemplate, ErrorConditionalCallback errorConditionalCallback) {
        return new IdempotentAspect(new RedisIdempotentRepository(redisTemplate, redisProperties), errorConditionalCallback);
    }

    @Bean
    public IdempotentAspect getIdempotentAspect(RedisTemplate redisTemplate) {
        return new IdempotentAspect(new RedisIdempotentRepository(redisTemplate, redisProperties));
    }

}