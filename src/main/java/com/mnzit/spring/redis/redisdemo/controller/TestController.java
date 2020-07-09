package com.mnzit.spring.redis.redisdemo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.Random;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
@Slf4j
@RestController
@RequestMapping("test")
public class TestController {

    @Autowired
    @Qualifier("redisTemplate2")
    private RedisTemplate redisTemplate2;

    @Autowired
    private RedisTemplate redisTemplate;

    private HashOperations<String, String, Object> hashOperations;

    @PostConstruct
    public void init() {
        log.debug("redisTemplate : {}", redisTemplate);
        log.debug("redisTemplate2 : {}", redisTemplate2);
        hashOperations = redisTemplate.opsForHash();
    }

    @GetMapping("getTest")
    public void get() {


        hashOperations.put("Person", random(), "This is " + random());
    }

    public String random() {

        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

}
