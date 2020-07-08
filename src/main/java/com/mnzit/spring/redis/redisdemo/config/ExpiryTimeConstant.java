package com.mnzit.spring.redis.redisdemo.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
public interface ExpiryTimeConstant {

    Map<String, Duration> EXPIRY = new HashMap<String, Duration>() {{
        put(Time.ONE_MIN, Duration.ofMinutes(1));
        put(Time.FIVE_MIN, Duration.ofMinutes(5));
    }};

    interface Time {
        String ONE_MIN = "ONE_MIN";
        String FIVE_MIN = "FIVE_MIN";
    }
}
