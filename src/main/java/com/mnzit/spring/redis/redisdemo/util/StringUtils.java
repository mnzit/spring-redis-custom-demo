package com.mnzit.spring.redis.redisdemo.util;

import org.springframework.lang.Nullable;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
public class StringUtils {

    public static boolean isNullOrEmpty(@Nullable String string) {
        return string == null || string.isEmpty();
    }
}
