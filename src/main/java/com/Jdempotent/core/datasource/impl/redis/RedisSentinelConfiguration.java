package com.Jdempotent.core.datasource.impl.redis;


import com.Jdempotent.core.model.IdempotentResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

/**
 *
 */
@Configuration
public class RedisSentinelConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisConfiguration.class);

    private final RedisConfigProperties redisProperties;

    public RedisSentinelConfiguration(RedisConfigProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        org.springframework.data.redis.connection.RedisSentinelConfiguration sentinelConfiguration = new org.springframework.data.redis.connection.RedisSentinelConfiguration()
                .master(redisProperties.getSentinelMasterName());
        redisProperties.getSentinelHostList().forEach(
                host -> sentinelConfiguration.sentinel(host, redisProperties.getSentinelPort()));
        sentinelConfiguration.setPassword(redisProperties.getPassword());
        return new LettuceConnectionFactory(sentinelConfiguration,
                org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
                        .defaultConfiguration());
    }

    @Bean
    public RedisTemplate redisTemplate() {
        RedisTemplate<String, IdempotentResponseWrapper> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory());
        redisTemplate.afterPropertiesSet();
        redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }
}
