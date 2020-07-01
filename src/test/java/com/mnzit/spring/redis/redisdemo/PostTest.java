package com.mnzit.spring.redis.redisdemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
@Slf4j
public class PostTest {

    public static void getPostById(Long id) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = "http://localhost:8080/posts/";
        ResponseEntity<String> response
                = restTemplate.getForEntity(fooResourceUrl + id, String.class);

        log.debug("Response: {}", response);
    }
}
