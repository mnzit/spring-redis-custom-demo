package com.mnzit.spring.redis.redisdemo.repository;

import com.mnzit.spring.redis.redisdemo.annotation.Cacheable;
import com.mnzit.spring.redis.redisdemo.entity.Post;
import com.mnzit.spring.redis.redisdemo.enums.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Cacheable(identifier = "POST", cacheName = "'post'", ttl = 3, type = Type.HASHMAP)
    @Override
    Page<Post> findAll(Pageable pageable);

    @Override
    Optional<Post> findById(Long postId);

}
