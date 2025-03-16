package com.wanderai.dto.merchant;

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
public class OrderDetailDto {
    private Long id;
    private String orderNumber;
    private PackageSimpleDto tourPackage;
    private UserSimpleDto user;
    private int quantity;
    private double totalAmount;
    private String status;
    private LocalDateTime createdAt;
    private List<String> notes;
    private String cancelReason;
    private LocalDateTime confirmedAt;
    private LocalDateTime canceledAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PackageSimpleDto {
        private Long id;
        private String title;
        private double price;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSimpleDto {
        private Long id;
        private String userName;
        private String email;
        private String phone;
    }
}
