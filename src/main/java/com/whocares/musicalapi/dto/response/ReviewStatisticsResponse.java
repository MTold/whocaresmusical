package com.whocares.musicalapi.dto.response;

import lombok.Data;

public class ReviewStatisticsResponse {
    private Long totalCount = 0L;
    private Double averageRating = 0.0;
    private Long rating1Count = 0L;
    private Long rating2Count = 0L;
    private Long rating3Count = 0L;
    private Long rating4Count = 0L;
    private Long rating5Count = 0L;

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Long getRating1Count() {
        return rating1Count;
    }

    public void setRating1Count(Long rating1Count) {
        this.rating1Count = rating1Count;
    }

    public Long getRating2Count() {
        return rating2Count;
    }

    public void setRating2Count(Long rating2Count) {
        this.rating2Count = rating2Count;
    }

    public Long getRating3Count() {
        return rating3Count;
    }

    public void setRating3Count(Long rating3Count) {
        this.rating3Count = rating3Count;
    }

    public Long getRating4Count() {
        return rating4Count;
    }

    public void setRating4Count(Long rating4Count) {
        this.rating4Count = rating4Count;
    }

    public Long getRating5Count() {
        return rating5Count;
    }

    public void setRating5Count(Long rating5Count) {
        this.rating5Count = rating5Count;
    }
}