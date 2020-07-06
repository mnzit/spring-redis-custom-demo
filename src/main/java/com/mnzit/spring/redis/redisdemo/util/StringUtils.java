package com.mnzit.spring.redis.redisdemo.util;

import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
public class StringUtils {

    public static boolean isNullOrEmpty(@Nullable String string) {
        return string == null || string.isEmpty();
    }

    public static boolean isEmpty(@NotNull String string) {
        return string.isEmpty();
    }
}
