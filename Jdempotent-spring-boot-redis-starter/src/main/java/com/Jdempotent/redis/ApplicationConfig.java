package com.Jdempotent.redis;


import com.Jdempotent.core.aspect.IdempotentAspect;
import com.Jdempotent.core.callback.ErrorConditionalCallback;
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
    public IdempotentAspect getLogMethodExecutionTimeAspect(RedisTemplate redisTemplate, ErrorConditionalCallback errorConditionalCallback) {
        return new IdempotentAspect(new RedisIdempotentRepository(redisTemplate, redisProperties), errorConditionalCallback);
    }

    @Bean
    public IdempotentAspect getLogMethodExecutionTimeAspect(RedisTemplate redisTemplate) {
        return new IdempotentAspect(new RedisIdempotentRepository(redisTemplate, redisProperties));
    }

}