package com.whocares.musicalapi.service.impl;

import com.whocares.musicalapi.dto.request.ReviewRequest;
import com.whocares.musicalapi.dto.response.ReviewResponse;
import com.whocares.musicalapi.dto.response.ReviewStatisticsResponse;
import com.whocares.musicalapi.entity.Performance;
import com.whocares.musicalapi.entity.Review;
import com.whocares.musicalapi.entity.User;
import com.whocares.musicalapi.exception.ResourceNotFoundException;
import com.whocares.musicalapi.repository.PerformanceRepository;
import com.whocares.musicalapi.repository.ReviewRepository;
import com.whocares.musicalapi.repository.UserRepository;
import com.whocares.musicalapi.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final PerformanceRepository performanceRepository;

    // 在Repository接口中修改

    @Override
    public Page<Review> findByReviewStatus(Integer status, Pageable pageable) {
        // 方法1：直接使用JPA方法名查询
        return reviewRepository.findByReviewStatus(status, pageable);}

    public ReviewServiceImpl(ReviewRepository reviewRepository, UserRepository userRepository, PerformanceRepository performanceRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.performanceRepository = performanceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsByPerformance(Long performanceId, int page, int size, Integer rating) {
        validatePerformanceExists(performanceId);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Review> reviewsPage;
        
        if (rating != null) {
            reviewsPage = reviewRepository.findByPerformanceIdAndRatingOrderByCreatedAtDesc(performanceId, rating, pageable);
        } else {
            reviewsPage = reviewRepository.findByPerformanceIdOrderByCreatedAtDesc(performanceId, pageable);
        }
        
        return reviewsPage.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsByUser(String username, int page, int size) {
        User user = getUserByUsername(username);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Review> reviewsPage = reviewRepository.findByUserIdOrderByCreatedAtDesc(user.getUserId(), pageable);
        
        return reviewsPage.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewStatisticsResponse getReviewStatistics(Long performanceId) {
        validatePerformanceExists(performanceId);
        
        List<Object[]> results = reviewRepository.getReviewStatistics(performanceId);
        
        ReviewStatisticsResponse response = new ReviewStatisticsResponse();
        
        if (results.isEmpty() || results.get(0)[0] == null) {
            response.setTotalCount(0L);
            response.setAverageRating(0.0);
            response.setRating1Count(0L);
            response.setRating2Count(0L);
            response.setRating3Count(0L);
            response.setRating4Count(0L);
            response.setRating5Count(0L);
            return response;
        }

        Object[] result = results.get(0);
        Long totalCount = ((Number) result[0]).longValue();
        Double averageRating = result[1] != null ? ((Number) result[1]).doubleValue() : 0.0;

        // 计算各等级评分数量
        RatingCounts counts = getRatingCounts(performanceId);

        response.setTotalCount(totalCount);
        response.setAverageRating(Math.round(averageRating * 10.0) / 10.0); // 保留一位小数
        response.setRating1Count(counts.getRating1());
        response.setRating2Count(counts.getRating2());
        response.setRating3Count(counts.getRating3());
        response.setRating4Count(counts.getRating4());
        response.setRating5Count(counts.getRating5());

        return response;
    }

    @Override
    public ReviewResponse createReview(ReviewRequest reviewRequest, String username) {
        // 如果是匿名用户，不查找User实体，直接创建评论
        Performance performance = getPerformance(reviewRequest.getPerformanceId());
        
        Review review = new Review();
        review.setContent(reviewRequest.getContent());
        review.setRating(reviewRequest.getRating());
        review.setReviewStatus(1); // 默认状态为正常
        
        // 针对匿名用户，设置user为null，让数据库user_id为null
        if (!username.equals("anonymous")) {
            User user = getUserByUsername(username);
            review.setUser(user);
            // 检查是否已经评价过
            if (hasUserReviewed(username, reviewRequest.getPerformanceId())) {
                throw new IllegalStateException("用户已经对该剧目评价过");
            }
        } else {
            // 匿名用户直接创建评论，不做重复检查
            review.setUser(null);
        }
        review.setPerformance(performance);
        
        review = reviewRepository.save(review);
        return convertToResponse(review);
    }

    @Override
    public ReviewResponse updateReview(Long reviewId, ReviewRequest reviewRequest, String username) {
        Review review = getReview(reviewId);
        
        // 对于已登录用户的验证逻辑（暂时禁用）
        if (!username.equals("anonymous")) {
            User user = getUserByUsername(username);
            // 验证是否是评价所有者 - 暂时禁用
            if (review.getUser() != null && !review.getUser().getUserId().equals(user.getUserId())) {
                throw new IllegalStateException("只能修改自己的评价");
            }
        }
        // 匿名用户允许更新任何评价（临时方案）
        
        // 验证是否还是同一个剧目（不允许更改评价的剧目）
        if (!review.getPerformance().getId().equals(reviewRequest.getPerformanceId())) {
            throw new IllegalArgumentException("不能更改评价的剧目");
        }
        
        review.setContent(reviewRequest.getContent());
        review.setRating(reviewRequest.getRating());
        
        review = reviewRepository.save(review);
        return convertToResponse(review);
    }


    @Override
    @Transactional
    public void deleteReview(Long reviewId, String username, Iterable<? extends GrantedAuthority> authorities) {
        Review review = getReview(reviewId);
        
        // 对于已登录用户的验证逻辑（暂时禁用）
        if (!username.equals("anonymous")) {
            User user = getUserByUsername(username);
            
            // 检查是否是评价所有者或管理员 - 暂时禁用或简化
            if (review.getUser() != null && !review.getUser().getUserId().equals(user.getUserId())) {
                boolean isAdmin = false;
                for (GrantedAuthority authority : authorities) {
                    if (authority.getAuthority().equals("ROLE_ADMIN")) {
                        isAdmin = true;
                        break;
                    }
                }
                if (!isAdmin) {
                    throw new IllegalStateException("只能删除自己的评价或管理员删除");
                }
            }
        }
        // 匿名用户允许删除任何评价（临时方案）
        
        reviewRepository.delete(review);
    }

    @Override
    public boolean hasUserReviewed(String username, Long performanceId) {
        if (username == null || username.equals("anonymous")) {
            return false; // 匿名用户或未登录用户
        }
        
        User user = getUserByUsername(username);
        Review existingReview = reviewRepository.findByUserIdAndPerformanceId(user.getUserId(), performanceId);
        return existingReview != null;
    }

    private ReviewResponse convertToResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        
        // Safe field mapping with null checks
        if (review != null) {
            response.setId(review.getId());
            response.setContent(review.getContent());
            response.setRating(review.getRating());
            response.setCreatedAt(review.getCreatedAt());
            response.setPerformanceId(review.getPerformance() != null ? review.getPerformance().getId() : null);
            response.setStatus(review.getReviewStatus()); // 添加状态字段
            
            if (review.getUser() != null) {
                response.setUserId(review.getUser().getUserId());
                response.setUsername(review.getUser().getUsername());
                response.setUserImage(review.getUser().getUserImage());
            } else {
                // 匿名用户的情况
                response.setUserId(null);
                response.setUsername("匿名用户");
                response.setUserImage(null);
            }
            
            if (review.getPerformance() != null) {
                response.setPerformanceName(review.getPerformance().getName());
            }
        }
        
        return response;
    }

    private RatingCounts getRatingCounts(Long performanceId) {
        RatingCounts counts = new RatingCounts();
        counts.rating1 = reviewRepository.findByPerformanceIdAndRatingOrderByCreatedAtDesc(performanceId, 1, Pageable.unpaged()).getTotalElements();
        counts.rating2 = reviewRepository.findByPerformanceIdAndRatingOrderByCreatedAtDesc(performanceId, 2, Pageable.unpaged()).getTotalElements();
        counts.rating3 = reviewRepository.findByPerformanceIdAndRatingOrderByCreatedAtDesc(performanceId, 3, Pageable.unpaged()).getTotalElements();
        counts.rating4 = reviewRepository.findByPerformanceIdAndRatingOrderByCreatedAtDesc(performanceId, 4, Pageable.unpaged()).getTotalElements();
        counts.rating5 = reviewRepository.findByPerformanceIdAndRatingOrderByCreatedAtDesc(performanceId, 5, Pageable.unpaged()).getTotalElements();
        return counts;
    }

    private void validatePerformanceExists(Long performanceId) {
        // 不再抛出异常，而是允许自动创建缺失的Performance
        // 由getPerformance方法处理缺失情况
    }

    private Review getReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("评价不存在: " + reviewId));
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在: " + username));
    }

    private Performance getPerformance(Long performanceId) {
        return performanceRepository.findById(performanceId)
                .orElseThrow(() -> new ResourceNotFoundException("剧目不存在: " + performanceId));
    }

    private static class RatingCounts {
        long rating1, rating2, rating3, rating4, rating5;
        
        long getRating1() { return rating1; }
        long getRating2() { return rating2; }
        long getRating3() { return rating3; }
        long getRating4() { return rating4; }
        long getRating5() { return rating5; }
    }
}