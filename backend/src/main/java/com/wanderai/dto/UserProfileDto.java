package com.wanderai.dto;

import com.wanderai.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private Long id;
    private String userName;
    private String email;
    private String bio;
    private String avatar;
    private Set<Role> roles;
    private String phone;
    private String address;
    private String businessLicense;
    private String merchantStatus;
    private LocalDateTime createdAt;
}