package com.wanderai.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsDto {
    private long totalUsers;
    private long totalPosts;
    private long totalComments;
    private long newUsersToday;
    private long newPostsToday;
    private long totalMerchants;
    private long pendingMerchants;
    private long totalPackages;
    private long pendingPackages;
    private long totalSales;
    private long salesToday;
}
