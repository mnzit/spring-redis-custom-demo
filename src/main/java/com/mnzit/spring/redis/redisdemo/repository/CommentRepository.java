package com.mnzit.spring.redis.redisdemo.repository;

import com.mnzit.spring.redis.redisdemo.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostId(Long postId);

    @Override
    List<Comment> findAll();
}
