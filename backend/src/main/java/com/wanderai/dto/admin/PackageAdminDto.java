package com.wanderai.dto.admin;

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
public class PackageAdminDto {
    private Long id;
    private String title;
    private String description;
    private double price;
    private double originalPrice;
    private String location;
    private String duration;
    private MerchantSimpleDto merchant;
    private String status;
    private int soldCount;
    private LocalDateTime createdAt;
    private List<String> features;
    private List<ItineraryDto> itinerary;
    private List<String> includes;
    private List<String> excludes;
    private List<String> notes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MerchantSimpleDto {
        private Long id;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItineraryDto {
        private int day;
        private String title;
        private String description;
    }
}
