package com.wanderai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendVerificationCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("WanderAI - 邮箱验证码");
        message.setText("您好，\n\n您的验证码是: " + code + "\n\n此验证码将在15分钟后过期。\n\n如果这不是您的操作，请忽略此邮件。\n\n祝您旅途愉快，\nWanderAI团队");
        
        mailSender.send(message);
    }
}