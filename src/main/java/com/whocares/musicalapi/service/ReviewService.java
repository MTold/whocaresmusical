package com.whocares.musicalapi.service;

import com.whocares.musicalapi.dto.request.ReviewRequest;
import com.whocares.musicalapi.dto.response.ReviewResponse;
import com.whocares.musicalapi.dto.response.ReviewStatisticsResponse;
import com.whocares.musicalapi.entity.Review;
import com.whocares.musicalapi.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ReviewService {
    
    Page<ReviewResponse> getReviewsByMusical(Long musicalId, int page, int size, Integer rating);
    
    Page<ReviewResponse> getReviewsByUser(String username, int page, int size);


    // 添加新的方法来按状态分类获取所有评价
    //List<ReviewResponse> getReviewsByStatus(Integer status, Pageable pageable);


    Page<Review> findByReviewStatus(Integer status, Pageable pageable); // 接口声明
    
    ReviewStatisticsResponse getReviewStatistics(Long musicalId);
    
    ReviewResponse createReview(ReviewRequest reviewRequest, String username);
    
    ReviewResponse updateReview(Long reviewId, ReviewRequest reviewRequest, String username);
    
    void deleteReview(Long reviewId, String username, Iterable<? extends GrantedAuthority> authorities);
    
    boolean hasUserReviewed(String username, Long musicalId);
}