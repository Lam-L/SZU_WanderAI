package com.wanderai.dto.merchant;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackageUpdateDto {
    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题不能超过100个字符")
    private String title;

    @NotBlank(message = "描述不能为空")
    private String description;

    @Min(value = 0, message = "价格不能为负数")
    private double price;

    @Min(value = 0, message = "原价不能为负数")
    private double originalPrice;

    @NotBlank(message = "目的地不能为空")
    private String location;

    @NotBlank(message = "行程天数不能为空")
    private String duration;

    @NotEmpty(message = "产品特色不能为空")
    private List<String> features;

    @NotEmpty(message = "行程安排不能为空")
    private List<ItineraryDto> itinerary;

    @NotEmpty(message = "费用包含不能为空")
    private List<String> includes;

    @NotEmpty(message = "费用不含不能为空")
    private List<String> excludes;

    private List<String> notes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItineraryDto {
        private int day;
        private String title;
        private String description;
    }
}
