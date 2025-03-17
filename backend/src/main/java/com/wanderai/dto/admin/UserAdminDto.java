package com.wanderai.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAdminDto {
    private Long id;
    private String userName;
    private String email;
    private String bio;
    private String avatar;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private boolean blocked;
    private int postCount;
    private int commentCount;
}
