package com.mnzit.spring.redis.redisdemo.controller;

import com.mnzit.spring.redis.redisdemo.entity.Comment;
import com.mnzit.spring.redis.redisdemo.exception.ResourceNotFoundException;
import com.mnzit.spring.redis.redisdemo.repository.CommentRepository;
import com.mnzit.spring.redis.redisdemo.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
@RestController
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @GetMapping("/posts/{postId}/comments")
    public List<Comment> getAllCommentsByPostId(@PathVariable(value = "postId") Long postId) {

        return commentRepository.findByPostId(postId);
    }

    @GetMapping("/comments")
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    @PostMapping("/posts/{postId}/comments")
    public Comment createComment(@PathVariable(value = "postId") Long postId,
                                 @Valid @RequestBody Comment comment) {
        return postRepository.findById(postId).map(post -> {
            comment.setPost(post);
            return commentRepository.save(comment);
        }).orElseThrow(() -> new ResourceNotFoundException("PostId " + postId + " not found"));
    }

    @PutMapping("/posts/{postId}/comments/{commentId}")
    public Comment updateComment(@PathVariable(value = "postId") Long postId,
                                 @PathVariable(value = "commentId") Long commentId,
                                 @Valid @RequestBody Comment commentRequest) {
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("PostId " + postId + " not found");
        }

        return commentRepository.findById(commentId).map(comment -> {
            comment.setText(commentRequest.getText());
            return commentRepository.save(comment);
        }).orElseThrow(() -> new ResourceNotFoundException("CommentId " + commentId + "not found"));
    }

}
