package com.whocares.musicalapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopReviewResponse {
    private Long id;
    private String content;
    private Long userId;
    private String username;
    private String userImage;
    private LocalDateTime createdAt;
    private Long shopId;
    private String shopName;
    private Integer status; // 新增：审核状态 (0-待审核 1-已通过 2-违规)

    public String getFormattedDate() {
        return createdAt.toString(); // 或使用DateTimeFormatter格式化
    }
}
