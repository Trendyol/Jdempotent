package com.trendyol.jdempotent.cassandra;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;

import java.util.List;

// DÜZELTME: Prefix "jdempotent.cassandra" olmalı, "jdempotent.cassandra" değil
@Primary
@ConfigurationProperties(prefix = "jdempotent.cassandra")
public class CassandraConfigProperties {

    private boolean enable;
    private List<String> contactPoints;
    private int port = 9042;
    private String localDatacenter;
    private String keyspace;
    private String username;
    private String password;

    // Getter ve Setter'lar
    public boolean isEnable() {
        return enable;
    }
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public List<String> getContactPoints() {
        return contactPoints;
    }
    public void setContactPoints(List<String> contactPoints) {
        this.contactPoints = contactPoints;
    }

    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }

    public String getLocalDatacenter() {
        return localDatacenter;
    }
    public void setLocalDatacenter(String localDatacenter) {
        this.localDatacenter = localDatacenter;
    }

    public String getKeyspace() {
        return keyspace;
    }
    public void setKeyspace(String keyspace) {
        this.keyspace = keyspace;
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

    @Override
    public String toString() {
        return "CassandraConfigProperties{" +
                "enable=" + enable +
                ", contactPoints=" + contactPoints +
                ", port=" + port +
                ", localDatacenter='" + localDatacenter + '\'' +
                ", keyspace='" + keyspace + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}