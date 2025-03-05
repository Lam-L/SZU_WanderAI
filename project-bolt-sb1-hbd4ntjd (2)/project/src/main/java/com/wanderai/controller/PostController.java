package com.wanderai.controller;

import com.wanderai.dto.CommentDto;
import com.wanderai.dto.PostCreateDto;
import com.wanderai.dto.PostDetailDto;
import com.wanderai.dto.PostSummaryDto;
import com.wanderai.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<Page<PostSummaryDto>> getAllPosts(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(postService.getAllPosts(pageable));
    }

    @GetMapping("/user")
    public ResponseEntity<List<PostSummaryDto>> getUserPosts() {
        return ResponseEntity.ok(postService.getUserPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDetailDto> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @PostMapping
    public ResponseEntity<PostDetailDto> createPost(
            @Valid @RequestPart("post") PostCreateDto postCreateDto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return ResponseEntity.ok(postService.createPost(postCreateDto, images));
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Long postId,
            @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(postService.addComment(postId, request.get("content")));
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Map<String, Boolean>> toggleLike(@PathVariable Long postId) {
        boolean liked = postService.toggleLike(postId);
        return ResponseEntity.ok(Map.of("liked", liked));
    }
}