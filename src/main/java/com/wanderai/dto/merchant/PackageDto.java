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
public class PackageDto {
    private Long id;
    private String title;
    private String description;
    private double price;
    private double originalPrice;
    private String location;
    private String duration;
    private String status;
    private int soldCount;
    private LocalDateTime createdAt;
}
