package com.mnzit.spring.redis.redisdemo.repository;

import com.mnzit.spring.redis.redisdemo.annotation.Cacheable;
import com.mnzit.spring.redis.redisdemo.constants.KeyConstant;
import com.mnzit.spring.redis.redisdemo.entity.Comment;
import com.mnzit.spring.redis.redisdemo.enums.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * User hashmap if you have to store multiple cache as group
     *
     * @param postId
     * @return
     */
    @Cacheable(identifier = KeyConstant.COMMENTS, cacheName = KeyConstant.POST + ".concat(#postId)", ttl = 5, type = Type.HASHMAP)
    List<Comment> findByPostId(Long postId);

    /**
     * Use normal way if you have to store each cache separately
     *
     * @return
     */
    @Cacheable(cacheName = KeyConstant.COMMENTS_ALL, ttl = 5)
    @Override
    List<Comment> findAll();
}
