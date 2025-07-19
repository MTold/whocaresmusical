package com.whocares.musicalapi.service;

import com.whocares.musicalapi.dto.request.ReviewRequest;
import com.whocares.musicalapi.dto.response.ReviewResponse;
import com.whocares.musicalapi.dto.response.ReviewStatisticsResponse;
import org.springframework.data.domain.Page;
import org.springframework.security.core.GrantedAuthority;

public interface ReviewService {
    
    Page<ReviewResponse> getReviewsByPerformance(Long performanceId, int page, int size, Integer rating);
    
    Page<ReviewResponse> getReviewsByUser(String username, int page, int size);
    
    ReviewStatisticsResponse getReviewStatistics(Long performanceId);
    
    ReviewResponse createReview(ReviewRequest reviewRequest, String username);
    
    ReviewResponse updateReview(Long reviewId, ReviewRequest reviewRequest, String username);
    
    void deleteReview(Long reviewId, String username, Iterable<? extends GrantedAuthority> authorities);
    
    boolean hasUserReviewed(String username, Long performanceId);
}