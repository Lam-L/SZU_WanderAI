package com.wanderai.dto.admin;

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
public class PostAdminDto {
    private Long id;
    private String title;
    private String content;
    private List<String> images;
    private UserSimpleDto author;
    private LocalDateTime createdAt;
    private long likes;
    private long comments;
    private boolean hidden;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSimpleDto {
        private Long id;
        private String userName;
    }
}
