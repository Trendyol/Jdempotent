package com.trendyol.jdempotent.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigUtility {
    @Value("${jdempotent.cryptography.algorithm:md5}")
    private String algorithm;
}
