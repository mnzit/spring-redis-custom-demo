package com.mnzit.spring.redis.redisdemo.manager;

import com.mnzit.spring.redis.redisdemo.request.RedisCacheClearRequest;
import com.mnzit.spring.redis.redisdemo.service.RedisCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
@Slf4j
@Component
public class RedisCacheClearManager {

    @Autowired
    private RedisCacheService redisCacheService;

    public Boolean clearAllCache(RedisCacheClearRequest redisCacheClearRequest) {
        try {
            if (redisCacheClearRequest.getType().equalsIgnoreCase("ALL")) {
                return redisCacheService.clearAll();
            } else if (redisCacheClearRequest.getKey() != null) {
                return redisCacheService.clear(new String[]{redisCacheClearRequest.getType()}, redisCacheClearRequest.getKey());
            }
            return redisCacheService.clear(new String[]{redisCacheClearRequest.getType()});
        } catch (Exception e) {
            log.error("Exception  {}", e.getMessage());
            return false;
        }
    }
}
