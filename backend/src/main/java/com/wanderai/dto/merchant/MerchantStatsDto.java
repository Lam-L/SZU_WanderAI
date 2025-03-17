package com.wanderai.dto.merchant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantStatsDto {
    private int totalPackages;
    private int activePackages;
    private int pendingPackages;
    private int totalOrders;
    private int pendingOrders;
    private double totalSales;
    private double todaySales;
    private double monthSales;
}
