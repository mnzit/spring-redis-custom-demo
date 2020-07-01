package com.mnzit.spring.redis.redisdemo;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
@Slf4j
public class PostTask implements Runnable {
    private final String name;
    private final Long id;

    public PostTask(String name, Long id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            for (int i = 1; i <= 50; i++) {
                PostTest.getPostById(id);
            }
        } catch (Exception e) {
            log.error("Exception ", e);
        }
    }
}
