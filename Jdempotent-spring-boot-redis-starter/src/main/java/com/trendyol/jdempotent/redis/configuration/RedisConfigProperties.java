package com.trendyol.jdempotent.redis.configuration;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 *
 */
@Configuration
@ConditionalOnProperty(
        prefix="jdempotent", name = "enable",
        havingValue = "true",
        matchIfMissing = true)
public class RedisConfigProperties {

    @Value("${jdempotent.cache.redis.database:0}")
    private Integer database;

    @Value("${jdempotent.cache.redis.sentinelPort}")
    private Integer sentinelPort;

    @Value("${jdempotent.cache.redis.password}")
    private String password;

    @Value("${jdempotent.cache.redis.sentinelMasterName}")
    private String sentinelMasterName;

    @Value("${jdempotent.cache.redis.sentinelHostList}")
    private List<String> sentinelHostList;

    @Value("${jdempotent.cache.redis.expirationTimeHour}")
    private Long expirationTimeHour;

    @Value("${jdempotent.cache.redis.dialTimeoutSecond}")
    private String dialTimeoutSecond;

    @Value("${jdempotent.cache.redis.readTimeoutSecond}")
    private String readTimeoutSecond;

    @Value("${jdempotent.cache.redis.writeTimeoutSecond}")
    private String writeTimeoutSecond;

    @Value("${jdempotent.cache.redis.maxRetryCount}")
    private String maxRetryCount;

    @Value("${jdempotent.cache.persistReqRes:true}")
    private Boolean persistReqRes;

    public Boolean getPersistReqRes() {
        return persistReqRes;
    }

    public void setPersistReqRes(Boolean persistReqRes) {
        this.persistReqRes = persistReqRes;
    }

    public Integer getSentinelPort() {
        return sentinelPort;
    }

    public void setSentinelPort(Integer sentinelPort) {
        this.sentinelPort = sentinelPort;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSentinelMasterName() {
        return sentinelMasterName;
    }

    public void setSentinelMasterName(String sentinelMasterName) {
        this.sentinelMasterName = sentinelMasterName;
    }

    public List<String> getSentinelHostList() {
        return sentinelHostList;
    }

    public void setSentinelHostList(List<String> sentinelHostList) {
        this.sentinelHostList = sentinelHostList;
    }

    public Long getExpirationTimeHour() {
        return expirationTimeHour;
    }

    public void setExpirationTimeHour(Long expirationTimeHour) {
        this.expirationTimeHour = expirationTimeHour;
    }

    public String getDialTimeoutSecond() {
        return dialTimeoutSecond;
    }

    public void setDialTimeoutSecond(String dialTimeoutSecond) {
        this.dialTimeoutSecond = dialTimeoutSecond;
    }

    public String getReadTimeoutSecond() {
        return readTimeoutSecond;
    }

    public void setReadTimeoutSecond(String readTimeoutSecond) {
        this.readTimeoutSecond = readTimeoutSecond;
    }

    public String getWriteTimeoutSecond() {
        return writeTimeoutSecond;
    }

    public void setWriteTimeoutSecond(String writeTimeoutSecond) {
        this.writeTimeoutSecond = writeTimeoutSecond;
    }

    public String getMaxRetryCount() {
        return maxRetryCount;
    }

    public void setMaxRetryCount(String maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public Integer getDatabase() {
        return database;
    }

    public void setDatabase(Integer database) {
        this.database = database;
    }
}
