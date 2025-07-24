package com.trendyol.jdempotent.cassandra;

import com.trendyol.jdempotent.core.aspect.IdempotentAspect;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.core.CassandraTemplate;

@Configuration
@EnableConfigurationProperties(CassandraConfigProperties.class) // Bu satırı ekledik
@ConditionalOnProperty(
        prefix="jdempotent", name = "enable",
        havingValue = "true",
        matchIfMissing = true)
public class ApplicationConfig {

    private final CassandraConfigProperties _cassandraConfigProperties;

    public ApplicationConfig(CassandraConfigProperties cassandraConfigProperties) {
        this._cassandraConfigProperties = cassandraConfigProperties;

        // Debug için
        System.out.println("ApplicationConfig constructor - CassandraConfigProperties: " +
                cassandraConfigProperties.toString());
    }

    @Bean
    public IdempotentAspect getIdempotentAspect(CassandraTemplate cassandraTemplate) {
        return new IdempotentAspect(new CassandraIdempotentRepository(cassandraTemplate, _cassandraConfigProperties));
    }
}