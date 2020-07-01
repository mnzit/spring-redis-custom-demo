package com.mnzit.spring.redis.redisdemo.repository;

import com.mnzit.spring.redis.redisdemo.annotation.Cacheable;
import com.mnzit.spring.redis.redisdemo.entity.Comment;
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
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Cacheable(identifier = "COMMENTS", cacheName = "'post:'.concat(#postId)", ttl = 3, type = Type.HASHMAP, condition = "#postId > 1044")
    Page<Comment> findByPostId(Long postId, Pageable pageable);

    Optional<Comment> findByIdAndPostId(Long id, Long postId);
}
