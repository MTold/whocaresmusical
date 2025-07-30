package com.whocares.musicalapi.service;

import com.whocares.musicalapi.dto.request.ReviewRequest;
import com.whocares.musicalapi.dto.response.ReviewResponse;
import com.whocares.musicalapi.dto.response.ReviewStatisticsResponse;
import com.whocares.musicalapi.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;

public interface ReviewService {

    Page<ReviewResponse> getReviewsByMusical(Long musicalId, int page, int size, Integer rating);

    Page<ReviewResponse> getReviewsByUser(String username, int page, int size);

    // 添加新的方法来按状态分类获取所有评价
    //List<ReviewResponse> getReviewsByStatus(Integer status, Pageable pageable);


    Page<Review> findByReviewStatus(Integer status, Pageable pageable); // 接口声明
    // 根据评价状态获取评价（带关联信息）
    Page<ReviewResponse> getReviewsByStatus(Integer status, Pageable pageable);
    ReviewStatisticsResponse getReviewStatistics(Long performanceId);

    ReviewResponse createReview(ReviewRequest reviewRequest, String username);

    ReviewResponse updateReview(Long reviewId, ReviewRequest reviewRequest, String username);

    ReviewResponse updateReviewStatus(Long reviewId, Integer status, String username);

    void deleteReview(Long reviewId, String username, Iterable<? extends GrantedAuthority> authorities);

    boolean hasUserReviewed(String username, Long performanceId);
}