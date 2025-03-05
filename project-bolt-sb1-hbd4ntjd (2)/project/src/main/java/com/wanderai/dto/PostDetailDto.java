package com.wanderai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailDto {
    private Long id;
    private String title;
    private String content;
    private List<String> images;
    private AuthorDto author;
    private LocalDateTime createdAt;
    private long likes;
    private List<CommentDto> comments;
    private boolean liked;
}