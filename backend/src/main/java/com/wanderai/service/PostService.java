package com.wanderai.service;

import com.wanderai.dto.CommentDto;
import com.wanderai.dto.PostCreateDto;
import com.wanderai.dto.PostDetailDto;
import com.wanderai.dto.PostSummaryDto;
import com.wanderai.dto.AuthorDto;
import com.wanderai.exception.ResourceNotFoundException;
import com.wanderai.model.Comment;
import com.wanderai.model.Post;
import com.wanderai.model.PostImage;
import com.wanderai.model.User;
import com.wanderai.model.Like;
import com.wanderai.repository.CommentRepository;
import com.wanderai.repository.LikeRepository;
import com.wanderai.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final UserService userService;
    private final FileStorageService fileStorageService;

    public Page<PostSummaryDto> getAllPosts(Pageable pageable) {
        return postRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::mapPostToSummaryDto);
    }

    public List<PostSummaryDto> getUserPosts() {
        User currentUser = userService.getCurrentUser();
        return postRepository.findByAuthorOrderByCreatedAtDesc(currentUser)
                .stream()
                .map(this::mapPostToSummaryDto)
                .collect(Collectors.toList());
    }

    public PostDetailDto getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        
        List<Comment> comments = commentRepository.findByPostOrderByCreatedAtDesc(post);
        
        return mapPostToDetailDto(post, comments);
    }

    @Transactional
    public PostDetailDto createPost(PostCreateDto postCreateDto, List<MultipartFile> images) {
        User currentUser = userService.getCurrentUser();
        
        Post post = Post.builder()
                .title(postCreateDto.getTitle())
                .content(postCreateDto.getContent())
                .author(currentUser)
                .build();
        
        Post savedPost = postRepository.save(post);
        
        // Handle image uploads
        if (images != null && !images.isEmpty()) {
            List<PostImage> postImages = new ArrayList<>();
            
            for (MultipartFile image : images) {
                String fileName = fileStorageService.storeFile(image);
                String fileDownloadUri = fileStorageService.getFileDownloadUri(fileName);
                
                PostImage postImage = PostImage.builder()
                        .imageUrl(fileDownloadUri)
                        .post(savedPost)
                        .build();
                
                postImages.add(postImage);
            }
            
            savedPost.setImages(postImages);
            savedPost = postRepository.save(savedPost);
        }
        
        return mapPostToDetailDto(savedPost, new ArrayList<>());
    }

    @Transactional
    public CommentDto addComment(Long postId, String content) {
        User currentUser = userService.getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        
        Comment comment = Comment.builder()
                .content(content)
                .post(post)
                .author(currentUser)
                .build();
        
        Comment savedComment = commentRepository.save(comment);
        
        return mapCommentToDto(savedComment);
    }

    @Transactional
    public boolean toggleLike(Long postId) {
        User currentUser = userService.getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        
        boolean liked = likeRepository.existsByPostAndUser(post, currentUser);
        
        if (liked) {
            // Unlike
            likeRepository.findByPostAndUser(post, currentUser)
                    .ifPresent(likeRepository::delete);
            return false;
        } else {
            // Like
            Like like = Like.builder()
                    .post(post)
                    .user(currentUser)
                    .build();
            likeRepository.save(like);
            return true;
        }
    }

    private PostSummaryDto mapPostToSummaryDto(Post post) {
        long likesCount = likeRepository.countByPost(post);
        long commentsCount = commentRepository.countByPost(post);
        
        List<String> imageUrls = post.getImages().stream()
                .map(PostImage::getImageUrl)
                .collect(Collectors.toList());
        
        return PostSummaryDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .images(imageUrls)
                .author(AuthorDto.builder()
                        .id(post.getAuthor().getId())
                        .userName(post.getAuthor().getUserName())
                        .build())
                .createdAt(post.getCreatedAt())
                .likes(likesCount)
                .comments(commentsCount)
                .build();
    }

    private PostDetailDto mapPostToDetailDto(Post post, List<Comment> comments) {
        long likesCount = likeRepository.countByPost(post);
        
        List<String> imageUrls = post.getImages().stream()
                .map(PostImage::getImageUrl)
                .collect(Collectors.toList());
        
        List<CommentDto> commentDtos = comments.stream()
                .map(this::mapCommentToDto)
                .collect(Collectors.toList());
        
        User currentUser = userService.getCurrentUser();
        boolean liked = likeRepository.existsByPostAndUser(post, currentUser);
        
        return PostDetailDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .images(imageUrls)
                .author(AuthorDto.builder()
                        .id(post.getAuthor().getId())
                        .userName(post.getAuthor().getUserName())
                        .build())
                .createdAt(post.getCreatedAt())
                .likes(likesCount)
                .comments(commentDtos)
                .liked(liked)
                .build();
    }

    private CommentDto mapCommentToDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .author(AuthorDto.builder()
                        .id(comment.getAuthor().getId())
                        .userName(comment.getAuthor().getUserName())
                        .build())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}