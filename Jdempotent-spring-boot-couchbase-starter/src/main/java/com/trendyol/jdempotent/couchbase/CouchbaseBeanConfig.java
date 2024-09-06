package com.trendyol.jdempotent.couchbase;

import com.couchbase.client.core.deps.io.netty.channel.epoll.EpollEventLoopGroup;
import com.couchbase.client.core.env.*;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ClusterOptions;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.codec.JacksonJsonSerializer;
import com.couchbase.client.java.env.ClusterEnvironment;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;

@Configuration
@ConditionalOnProperty(
        prefix="jdempotent", name = "enable",
        havingValue = "true",
        matchIfMissing = true)
public class CouchbaseBeanConfig {

    private final CouchbaseConfig couchbaseConfig;

    public CouchbaseBeanConfig(CouchbaseConfig couchbaseConfig) {
        this.couchbaseConfig = couchbaseConfig;
    }

    @Bean
    @Primary
    public Cluster cluster(ObjectMapper objectMapper) {
        var builder = ClusterEnvironment.builder();
        if (SystemUtils.IS_OS_LINUX) {
            builder.ioEnvironment(
                    IoEnvironment.kvEventLoopGroup(
                            new EpollEventLoopGroup(
                                    Runtime.getRuntime().availableProcessors() * 2
                            )
                    )
            )
                    .ioConfig(IoConfig.configPollInterval(Duration.ofSeconds(10)))
                    .securityConfig(SecurityConfig.enableNativeTls(false).enableTls(false));
        }
        var couchbaseEnvironment = builder
                .jsonSerializer(JacksonJsonSerializer.create(objectMapper))
                .timeoutConfig(
                        TimeoutConfig.kvTimeout(Duration.ofMillis(couchbaseConfig.getKvTimeout()))
                                .connectTimeout(Duration.ofMillis(couchbaseConfig.getConnectTimeout()))
                                .queryTimeout(Duration.ofMillis(couchbaseConfig.getQueryTimeout()))
                )
                .compressionConfig(CompressionConfig.enable(true))
                .loggerConfig(LoggerConfig.enableDiagnosticContext(false))
                .build();
        return Cluster.connect(
                couchbaseConfig.getConnectionString(),
                ClusterOptions.clusterOptions(couchbaseConfig.getUsername(), couchbaseConfig.getPassword())
                        .environment(couchbaseEnvironment)
        );
    }

    @Bean
    @Primary
    public Collection collection(ObjectMapper objectMapper) {
        return cluster(objectMapper).bucket(couchbaseConfig.getBucketName()).defaultCollection();
    }

    @Bean
    public ObjectMapper objectMapper() {
        var objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        return objectMapper;
    }
}
