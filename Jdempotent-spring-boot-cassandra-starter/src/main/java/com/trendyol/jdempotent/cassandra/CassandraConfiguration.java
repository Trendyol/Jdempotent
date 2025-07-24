package com.trendyol.jdempotent.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.core.CassandraTemplate;

import java.net.InetSocketAddress;

@Configuration
@EnableConfigurationProperties(CassandraConfigProperties.class)
public class CassandraConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraConfiguration.class);

    private final CassandraConfigProperties cassandraProperties;

    public CassandraConfiguration(CassandraConfigProperties cassandraProperties) {
        this.cassandraProperties = cassandraProperties;
    }

    @Bean
    @ConditionalOnProperty(value = "jdempotent.cassandra.enable", havingValue = "true")
    public CqlSession cqlSession(CassandraConfigProperties props) {
        return CqlSession.builder()
                .addContactPoint(new InetSocketAddress(props.getContactPoints().get(0), props.getPort()))
                .withLocalDatacenter(props.getLocalDatacenter())
                .withKeyspace(props.getKeyspace())
                .build();
    }

    @Bean
    public CassandraTemplate cassandraTemplate(CqlSession session) {
        return new CassandraTemplate(session);
    }
}