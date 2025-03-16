package com.wanderai.dto.merchant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MerchantUpdateDto {
    @NotBlank(message = "商家名称不能为空")
    private String name;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入有效的手机号")
    private String phone;

    @NotBlank(message = "地址不能为空")
    private String address;
}
