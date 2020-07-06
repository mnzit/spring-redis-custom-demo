package com.mnzit.spring.redis.redisdemo.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
@Data
public class RedisCacheClearRequest {
    @NotNull
    private String type;
    private String key;
}
