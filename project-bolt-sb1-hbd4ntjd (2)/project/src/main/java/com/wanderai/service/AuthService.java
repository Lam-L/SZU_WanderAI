package com.wanderai.service;

import com.wanderai.dto.JwtAuthResponse;
import com.wanderai.dto.LoginRequest;
import com.wanderai.dto.RegisterRequest;
import com.wanderai.dto.UserProfileDto;
import com.wanderai.exception.BadRequestException;
import com.wanderai.model.User;
import com.wanderai.model.VerificationCode;
import com.wanderai.repository.UserRepository;
import com.wanderai.repository.VerificationCodeRepository;
import com.wanderai.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final EmailService emailService;

    @Transactional
    public JwtAuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        
        // Update last login time
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadRequestException("用户不存在"));
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        
        return new JwtAuthResponse(jwt, mapUserToProfileDto(user));
    }

    @Transactional
    public JwtAuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("该邮箱已被注册");
        }

        // Verify the verification code
        Optional<VerificationCode> verificationCodeOpt = verificationCodeRepository
                .findByEmailAndCodeAndUsedFalse(registerRequest.getEmail(), registerRequest.getVerifyCode());
        
        if (verificationCodeOpt.isEmpty()) {
            throw new BadRequestException("验证码无效");
        }
        
        VerificationCode verificationCode = verificationCodeOpt.get();
        if (verificationCode.isExpired()) {
            throw new BadRequestException("验证码已过期，请重新获取");
        }
        
        // Mark the verification code as used
        verificationCode.setUsed(true);
        verificationCodeRepository.save(verificationCode);

        // Create new user
        User user = User.builder()
                .userName(registerRequest.getUserName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .build();

        User savedUser = userRepository.save(user);

        // Generate JWT token
        String jwt = tokenProvider.generateTokenFromEmail(savedUser.getEmail());
        
        return new JwtAuthResponse(jwt, mapUserToProfileDto(savedUser));
    }

    @Transactional
    public void sendVerificationCode(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("该邮箱已被注册");
        }

        // Generate a random 6-digit code
        String code = generateRandomCode();

        // Save the verification code
        VerificationCode verificationCode = VerificationCode.builder()
                .email(email)
                .code(code)
                .used(false)
                .build();
        
        verificationCodeRepository.save(verificationCode);

        // Send the verification code via email
        emailService.sendVerificationCode(email, code);
    }
    
    private String generateRandomCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Generates a number between 100000 and 999999
        return String.valueOf(code);
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