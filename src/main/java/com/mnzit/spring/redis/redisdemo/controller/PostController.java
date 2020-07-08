package com.mnzit.spring.redis.redisdemo.controller;

import com.mnzit.spring.redis.redisdemo.config.ExpiryTimeConstant;
import com.mnzit.spring.redis.redisdemo.dto.GenericResponse;
import com.mnzit.spring.redis.redisdemo.entity.Post;
import com.mnzit.spring.redis.redisdemo.exception.ResourceNotFoundException;
import com.mnzit.spring.redis.redisdemo.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
@RestController
public class PostController {

    @Autowired
    private PostRepository postRepository;


    @GetMapping("/posts")
    public ResponseEntity<?> getAllPosts() {
        return ResponseEntity.ok(GenericResponse.builder()
                .resultCode("0")
                .resultDescription("Posts Fetched Successfully")
                .data(postRepository.findAll())
                .build());
    }

    @Cacheable(value = "post", key = "#postId")
    @GetMapping("/posts/{postId}")
    public GenericResponse getPost(@PathVariable Long postId) {
        return GenericResponse.builder()
                .resultCode("0")
                .resultDescription("Post Fetched Successfully")
                .data(postRepository.findById(postId).get())
                .build();
    }

    @PostMapping("/posts")
    public Post createPost(@Valid @RequestBody Post post) {
        return postRepository.save(post);
    }

    @PutMapping("/posts/{postId}")
    public Post updatePost(@PathVariable Long postId, @Valid @RequestBody Post postRequest) {
        return postRepository.findById(postId).map(post -> {
            post.setTitle(postRequest.getTitle());
            post.setDescription(postRequest.getDescription());
            post.setContent(postRequest.getContent());
            return postRepository.save(post);
        }).orElseThrow(() -> new ResourceNotFoundException("PostId " + postId + " not found"));
    }


    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId) {
        return postRepository.findById(postId).map(post -> {
            postRepository.delete(post);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("PostId " + postId + " not found"));
    }
}