package com.mnzit.spring.redis.redisdemo.controller;

import com.mnzit.spring.redis.redisdemo.service.RedisCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
    private RedisCacheService redisCacheService;

    @GetMapping("/clear")
    public void clearCache() {
        redisCacheService.clearAll();
    }
}
