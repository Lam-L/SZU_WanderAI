package com.wanderai.controller;

import com.wanderai.dto.UserProfileDto;
import com.wanderai.dto.UserUpdateDto;
import com.wanderai.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getUserProfile() {
        return ResponseEntity.ok(userService.getUserProfile());
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileDto> updateUserProfile(@RequestBody UserUpdateDto userUpdateDto) {
        return ResponseEntity.ok(userService.updateUserProfile(userUpdateDto));
    }
}