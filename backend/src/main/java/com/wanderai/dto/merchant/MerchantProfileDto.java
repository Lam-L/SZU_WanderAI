package com.wanderai.dto.merchant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantProfileDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String businessLicense;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private int packageCount;
    private double totalSales;
}
