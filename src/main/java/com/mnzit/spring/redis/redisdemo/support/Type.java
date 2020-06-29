package com.mnzit.spring.redis.redisdemo.support;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
public enum Type {

    /**
     * null
     */
    NULL("null"),

    /**
     * string
     */
    STRING("string"),

    /**
     * object
     */
    OBJECT("Object object"),

    /**
     * List collection
     */
    LIST("List collection"),

    /**
     * Set collection
     */
    SET("Set Set"),

    /**
     * Array
     */
    ARRAY("array"),

    /**
     * Enumeration
     */
    ENUM("enum"),

    /**
     * Other types
     */
    OTHER("other types");

    private String label;

    Type(String label) {
        this.label = label;
    }

    public static Type parse(String name) {
        for (Type type : Type.values()) {
            if (type.name().equals(name)) {
                return type;
            }
        }
        return OTHER;
    }
}
