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
public class PostSummaryDto {
    private Long id;
    private String title;
    private List<String> images;
    private AuthorDto author;
    private LocalDateTime createdAt;
    private long likes;
    private long comments;
}