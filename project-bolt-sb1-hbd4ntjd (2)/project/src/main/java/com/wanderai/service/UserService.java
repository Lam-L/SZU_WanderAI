package com.wanderai.service;

import com.wanderai.dto.UserProfileDto;
import com.wanderai.dto.UserUpdateDto;
import com.wanderai.exception.ResourceNotFoundException;
import com.wanderai.model.User;
import com.wanderai.repository.UserRepository;
import com.wanderai.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    }

    public UserProfileDto getUserProfile() {
        User user = getCurrentUser();
        return mapUserToProfileDto(user);
    }

    @Transactional
    public UserProfileDto updateUserProfile(UserUpdateDto userUpdateDto) {
        User user = getCurrentUser();
        
        if (userUpdateDto.getUserName() != null && !userUpdateDto.getUserName().isEmpty()) {
            user.setUserName(userUpdateDto.getUserName());
        }
        
        if (userUpdateDto.getBio() != null) {
            user.setBio(userUpdateDto.getBio());
        }
        
        User updatedUser = userRepository.save(user);
        return mapUserToProfileDto(updatedUser);
    }

    private UserProfileDto mapUserToProfileDto(User user) {
        return UserProfileDto.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .bio(user.getBio())
                .avatar(user.getAvatar())
                .createdAt(user.getCreatedAt())
                .build();
    }
}