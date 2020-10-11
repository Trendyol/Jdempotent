package com.Jdempotent.redis;


import com.Jdempotent.core.aspect.IdempotentAspect;
import com.Jdempotent.core.callback.ErrorConditionalCallback;
import com.Jdempotent.redis.RedisIdempotentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 
 */
@Configuration
public class ApplicationConfig {

    @Bean
    @ConditionalOnClass(ErrorConditionalCallback.class)
    public IdempotentAspect getLogMethodExecutionTimeAspect(RedisTemplate redisTemplate, ErrorConditionalCallback errorConditionalCallback) {
        return new IdempotentAspect(new RedisIdempotentRepository(redisTemplate), errorConditionalCallback);
    }

    @Bean
    public IdempotentAspect getLogMethodExecutionTimeAspect(RedisTemplate redisTemplate) {
        return new IdempotentAspect(new RedisIdempotentRepository(redisTemplate));
    }

}