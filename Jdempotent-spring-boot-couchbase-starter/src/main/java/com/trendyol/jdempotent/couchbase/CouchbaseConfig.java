package com.trendyol.jdempotent.couchbase;

import com.couchbase.client.core.deps.io.netty.channel.epoll.EpollEventLoopGroup;
import com.couchbase.client.core.env.CompressionConfig;
import com.couchbase.client.core.env.IoConfig;
import com.couchbase.client.core.env.IoEnvironment;
import com.couchbase.client.core.env.LoggerConfig;
import com.couchbase.client.core.env.SecurityConfig;
import com.couchbase.client.core.env.TimeoutConfig;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ClusterOptions;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.codec.JacksonJsonSerializer;
import com.couchbase.client.java.env.ClusterEnvironment;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.context.annotation.Primary;

@ConditionalOnProperty(
        prefix="jdempotent", name = "enable",
        havingValue = "true",
        matchIfMissing = true)
@Configuration
public class CouchbaseConfig {
    @Value("${jdempotent.cache.couchbase.connection-string}")
    private String connectionString;
    @Value("${jdempotent.cache.couchbase.username}")
    private String username;
    @Value("${jdempotent.cache.couchbase.password}")
    private String password;
    @Value("${jdempotent.cache.couchbase.bucket-name}")
    private String bucketName;
    @Value("${jdempotent.cache.couchbase.connect-timeout}")
    private Long connectTimeout;
    @Value("${jdempotent.cache.couchbase.query-timeout}")
    private Long queryTimeout;
    @Value("${jdempotent.cache.couchbase.kv-timeout}")
    private Long kvTimeout;
    @Value("${jdempotent.cache.persistReqRes:true}")
    private Boolean persistReqRes;

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public Long getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Long getQueryTimeout() {
        return queryTimeout;
    }

    public void setQueryTimeout(Long queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    public Long getKvTimeout() {
        return kvTimeout;
    }

    public void setKvTimeout(Long kvTimeout) {
        this.kvTimeout = kvTimeout;
    }

    public Boolean getPersistReqRes() {
        return persistReqRes;
    }

    public void setPersistReqRes(Boolean persistReqRes) {
        this.persistReqRes = persistReqRes;
    }
}