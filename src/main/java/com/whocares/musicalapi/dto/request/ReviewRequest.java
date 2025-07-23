package com.whocares.musicalapi.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

public class ReviewRequest {
    @NotNull(message = "剧目ID不能为空")
    private Long musicalId;
    
    @NotBlank(message = "评价内容不能为空")
    @Size(min = 1, max = 1000, message = "评价内容长度需在1-1000字符之间")
    private String content;
    
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "最低评分为1分")
    @Max(value = 5, message = "最高评分为5分")
    private Integer rating;

    public Long getMusicalId() {
        return musicalId;
    }

    public void setMusicalId(Long musicalId) {
        this.musicalId = musicalId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}