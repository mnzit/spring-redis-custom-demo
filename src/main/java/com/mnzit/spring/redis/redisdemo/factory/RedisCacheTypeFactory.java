package com.mnzit.spring.redis.redisdemo.factory;

import com.mnzit.spring.redis.redisdemo.aspect.cacheType.RedisCache;
import com.mnzit.spring.redis.redisdemo.enums.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
@Component
public class RedisCacheTypeFactory {

    @Autowired
    @Qualifier("hashMapCache")
    private RedisCache hashMapCache;

    @Autowired
    @Qualifier("stringCache")
    private RedisCache stringCache;

   public RedisCache getCacheType(Type type){
        if (type.name().equals(Type.STRING.name())) {

          return stringCache;

        } else if (type.name().equals(Type.HASHMAP.name())) {

          return hashMapCache;

        } else {
            throw new IllegalArgumentException("Type not supported");
        }
    }

}
