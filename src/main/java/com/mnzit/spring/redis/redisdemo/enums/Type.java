package com.mnzit.spring.redis.redisdemo.enums;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
public enum Type {
    /**
     * STRING
     */
    STRING("string"),

    /**
     * HASHMAP
     */
    HASHMAP ("hash");

    private String label;

    Type(String label) {
        this.label = label;
    }
}
