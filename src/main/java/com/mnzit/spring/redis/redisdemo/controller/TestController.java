package com.mnzit.spring.redis.redisdemo.controller;

import com.mnzit.spring.redis.redisdemo.entity.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
@RestController
@RequestMapping("test")
public class TestController {

    AtomicLong atomicLong = new AtomicLong(1);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private HashOperations<String, Object, Object> hashOperations;

    @PostConstruct
    public void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    @GetMapping
    public void operation() {
        save();
    }


    public void save() {

        Post post = new Post();
        post.setId(atomicLong.incrementAndGet());
        post.setContent("contetnts");
        post.setDescription("description");
        post.setTitle("the tiutle");
        post.setCreatedAt(new Date());
        hashOperations.put("POSTS", post.getId().toString(), post);
    }
}
