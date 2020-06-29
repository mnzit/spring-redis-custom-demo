package com.mnzit.spring.redis.redisdemo.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
public class CacheUtil {

    private CacheUtil() {
    }

    /**
     * Takes a list of arguments and returns a cache key for given objects.
     */
    public static long buildCacheKey(Object... args) {
        StringBuilder key = new StringBuilder(":");
        for (Object obj : args) {
            if (obj != null) {
                key.append(obj.toString()).append(":");
            }
        }
        return DigestUtils.sha1Hex(key.toString()).hashCode();
    }

    public static String buildStringCacheKey(Object... args) {
        StringBuilder key = new StringBuilder(":");
        for (Object obj : args) {
            if (obj != null) {
                key.append(obj.toString()).append(":");
            }
        }
        return key.toString();
    }

    /**
     * Takes a list of arguments and returns a cache key for given objects.
     *
     * @param <T>
     */
    public static <T> long buildCacheKey(Iterable<T> args) {
        StringBuilder key = new StringBuilder(":");
        for (Object obj : args) {
            if (obj != null) {
                key.append(obj.toString()).append(":");
            }
        }
        return DigestUtils.sha1Hex(key.toString()).hashCode();
    }
}
