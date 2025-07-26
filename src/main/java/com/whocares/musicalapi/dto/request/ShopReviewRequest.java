package com.whocares.musicalapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ShopReviewRequest {
    @NotNull(message = "店铺名称不能为空")
    private String shopName;

    @NotBlank(message = "评价内容不能为空")
    @Size(min = 1, max = 1000, message = "评价内容长度需在1-1000字符之间")
    private String content;
}
