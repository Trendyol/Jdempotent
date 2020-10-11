package com.Jdempotent.core.constant;

/**
 *
 * Supported hash algorithms to generate idempotency key
 *
 */
public enum CryptographyAlgorithm {

    /**
     * use md5 hash algorithm
     */
    MD5("MD5"),

    /**
     * use SHA-256 hash algorithm
     */
    SHA256("SHA-256"),

    /**
     * use SHA-1 hash algorithm
     */
    SHA1("SHA-1");

    private String algorithm;

    CryptographyAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String value(){
        return algorithm;
    }
}
