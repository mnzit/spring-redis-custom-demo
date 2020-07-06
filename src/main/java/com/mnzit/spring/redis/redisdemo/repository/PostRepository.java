package com.mnzit.spring.redis.redisdemo.repository;

import com.mnzit.spring.redis.redisdemo.annotation.Cacheable;
import com.mnzit.spring.redis.redisdemo.constants.KeyConstant;
import com.mnzit.spring.redis.redisdemo.entity.Post;
import com.mnzit.spring.redis.redisdemo.enums.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    /**
     * Use normal way if you have to store each cache separately
     *
     * @return
     */
    @Cacheable(cacheName = KeyConstant.POSTS_ALL, ttl = 5)
    @Override
    List<Post> findAll();

}
