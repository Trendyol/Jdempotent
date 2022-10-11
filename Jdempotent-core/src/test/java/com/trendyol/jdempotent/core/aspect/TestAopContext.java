package com.trendyol.jdempotent.core.aspect;

import com.trendyol.jdempotent.core.datasource.InMemoryIdempotentRepository;
import com.trendyol.jdempotent.core.generator.DefaultKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = {"com.trendyol.jdempotent.core"})
public class TestAopContext {

    @Bean
    public IdempotentAspect idempotentAspect(InMemoryIdempotentRepository inMemoryIdempotentRepository, DefaultKeyGenerator defaultKeyGenerator) {
        return new IdempotentAspect(inMemoryIdempotentRepository, defaultKeyGenerator);
    }

    @Bean
    public InMemoryIdempotentRepository inMemoryIdempotentRepository() {
        return new InMemoryIdempotentRepository();
    }

    @Bean
    public DefaultKeyGenerator defaultKeyGenerator() {
        return new DefaultKeyGenerator();
    }

}
