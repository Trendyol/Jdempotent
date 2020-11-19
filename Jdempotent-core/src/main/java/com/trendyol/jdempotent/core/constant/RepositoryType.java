package com.trendyol.jdempotent.core.constant;

import java.util.Arrays;

/**
 *
 * Supported datasource types
 *
 */
public enum RepositoryType {

    /**
     *  Redis config value
     */
    REDIS("redis"),

    /**
     *  Hazelcast config value
     */
    HAZELCAST("hazelcast"),

    /**
     * Default config
     */
    INMEMORY("default");

    private String value;

    RepositoryType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    /**
     * return 
     * 
     * @param repositoryName
     * @return
     */
    public static RepositoryType getRepositoryTypeByValue(String repositoryName){
        return Arrays.stream(values()).filter(repositoryType -> repositoryName.equalsIgnoreCase(repositoryType.value)).findAny().orElse(RepositoryType.INMEMORY);
    }
}
