package com.mnzit.spring.redis.redisdemo.controller;

import com.mnzit.spring.redis.redisdemo.manager.RedisCacheClearManager;
import com.mnzit.spring.redis.redisdemo.request.RedisCacheClearRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
@RestController
@RequestMapping("redis/cache")
public class CacheClearController {
    @Autowired
    private RedisCacheClearManager redisCacheClearManager;

    @PostMapping("/clear")
    public Boolean clearAllCache(@RequestBody RedisCacheClearRequest redisCacheClearRequest) {
        return redisCacheClearManager.clearAllCache(redisCacheClearRequest);
    }
}
