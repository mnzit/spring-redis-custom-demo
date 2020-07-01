package com.mnzit.spring.redis.redisdemo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@SpringBootTest
class RedisDemoApplicationTests {

    @Test
    void postTest() {
        ExecutorService executor = Executors.newFixedThreadPool(2000);

        while(true) {
            for (long i = 1033; i <= 1047; i++) {

                executor.submit(new PostTask("thread" + i, i));
            }
        }
//        executor.shutdown();
//        while (!executor.isTerminated()) {
//        }
//        log.info("Total topup amount : " + sum);
    }

}
