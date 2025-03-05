package com.wanderai.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank
    @Size(max = 50)
    private String userName;

    @NotBlank
    @Size(max = 100)
    @Email
    private String email;

    @NotBlank
    private String verifyCode;

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;
}