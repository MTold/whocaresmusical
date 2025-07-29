package com.whocares.musicalapi.service.impl;

import com.whocares.musicalapi.dto.request.ReviewRequest;
import com.whocares.musicalapi.dto.response.ReviewResponse;
import com.whocares.musicalapi.dto.response.ReviewStatisticsResponse;
import com.whocares.musicalapi.entity.Musical;
import com.whocares.musicalapi.entity.Review;
import com.whocares.musicalapi.entity.User;
import com.whocares.musicalapi.exception.ResourceNotFoundException;
import com.whocares.musicalapi.repository.MusicalRepository;
import com.whocares.musicalapi.repository.ReviewRepository;
import com.whocares.musicalapi.repository.UserRepository;
import com.whocares.musicalapi.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MusicalRepository musicalRepository;

    // 在Repository接口中修改

    @Override
    public Page<Review> findByReviewStatus(Integer status, Pageable pageable) {
        // 方法1：直接使用JPA方法名查询
        return reviewRepository.findByReviewStatus(status, pageable);}

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsByStatus(Integer status, Pageable pageable) {
        // 使用带关联查询的方法获取评价数据
        Page<Review> reviewsPage = reviewRepository.findByStatusWithMusical(status, pageable);

        // 复用现有的convertToResponse方法进行数据转换
        return reviewsPage.map(this::convertToResponse);
    }

    public ReviewServiceImpl(ReviewRepository reviewRepository, UserRepository userRepository, MusicalRepository musicalRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.musicalRepository = musicalRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsByMusical(Long musicalId, int page, int size, Integer rating) {
        validateMusicalExists(musicalId);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Review> reviewsPage;

        if (rating != null) {
            // 使用已存在的方法，但需要手动过滤状态
            reviewsPage = reviewRepository.findByMusicalIdAndRatingOrderByCreatedAtDesc(musicalId, rating, pageable);
        } else {
            reviewsPage = reviewRepository.findByMusicalIdOrderByCreatedAtDesc(musicalId, pageable);
        }

        // 手动过滤已通过的评价（状态为1）
        List<Review> filteredReviews = reviewsPage.getContent().stream()
            .filter(review -> review.getReviewStatus() != null && review.getReviewStatus() == 1)
            .toList();
        
        return new PageImpl<>(filteredReviews, pageable, filteredReviews.size())
            .map(this::convertToResponse);
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
    public ReviewStatisticsResponse getReviewStatistics(Long musicalId) {
        validateMusicalExists(musicalId);

        List<Object[]> results = reviewRepository.getReviewStatistics(musicalId);

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
        long rating1Count    = result[2] != null ? ((Number) result[2]).longValue() : 0L;
        long rating2Count    = result[3] != null ? ((Number) result[3]).longValue() : 0L;
        long rating3Count    = result[4] != null ? ((Number) result[4]).longValue() : 0L;
        long rating4Count    = result[5] != null ? ((Number) result[5]).longValue() : 0L;
        long rating5Count    = result[6] != null ? ((Number) result[6]).longValue() : 0L;

        response.setTotalCount(totalCount);
        response.setAverageRating(Math.round(averageRating * 10.0) / 10.0);
        response.setRating1Count(rating1Count);
        response.setRating2Count(rating2Count);
        response.setRating3Count(rating3Count);
        response.setRating4Count(rating4Count);
        response.setRating5Count(rating5Count);
        return response;
    }

    @Override
    public ReviewResponse createReview(ReviewRequest reviewRequest, String username) {
        // 如果是匿名用户，不查找User实体，直接创建评论
        Musical musical = getMusical(reviewRequest.getMusicalId());

        Review review = new Review();
        review.setContent(reviewRequest.getContent());
        review.setRating(reviewRequest.getRating());
        review.setReviewStatus(1); // 默认状态为正常

        // 针对匿名用户，设置user为null，让数据库user_id为null
        if (!username.equals("anonymous")) {
            User user = getUserByUsername(username);
            review.setUser(user);
            // 检查是否已经评价过
            if (hasUserReviewed(username, reviewRequest.getMusicalId())) {
                throw new IllegalStateException("用户已经对该剧目评价过");
            }
        } else {
            // 匿名用户直接创建评论，不做重复检查
            review.setUser(null);
        }
        review.setMusical(musical);

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
        if (!review.getMusical().getId().equals(reviewRequest.getMusicalId())) {
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
    public ReviewResponse updateReviewStatus(Long reviewId, Integer status, String username) {
        Review review = getReview(reviewId);
        review.setReviewStatus(status);
        review = reviewRepository.save(review);
        return convertToResponse(review);
    }

    @Override
    public boolean hasUserReviewed(String username, Long musicalId) {
        if (username == null || username.equals("anonymous")) {
            return false; // 匿名用户或未登录用户
        }

        User user = getUserByUsername(username);
        Review existingReview = reviewRepository.findByUserIdAndMusicalId(user.getUserId(), musicalId);
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
            response.setMusicalId(review.getMusical() != null ? review.getMusical().getId() : null);
            response.setReviewStatus(review.getReviewStatus()); // 添加状态字段

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

            if (review.getMusical() != null) {
                response.setMusicalName(review.getMusical().getName());
            }
        }

        return response;
    }

    /*private RatingCounts getRatingCounts(Long musicalId) {
        RatingCounts counts = new RatingCounts();
        
        // 获取所有评价后手动过滤状态
        List<Review> allReviews = reviewRepository.findByMusicalIdOrderByCreatedAtDesc(musicalId, Pageable.unpaged()).getContent();
        
        counts.rating1 = allReviews.stream()
            .filter(r -> r.getReviewStatus() != null && r.getReviewStatus() == 1 && r.getRating() == 1)
            .count();
        counts.rating2 = allReviews.stream()
            .filter(r -> r.getReviewStatus() != null && r.getReviewStatus() == 1 && r.getRating() == 2)
            .count();
        counts.rating3 = allReviews.stream()
            .filter(r -> r.getReviewStatus() != null && r.getReviewStatus() == 1 && r.getRating() == 3)
            .count();
        counts.rating4 = allReviews.stream()
            .filter(r -> r.getReviewStatus() != null && r.getReviewStatus() == 1 && r.getRating() == 4)
            .count();
        counts.rating5 = allReviews.stream()
            .filter(r -> r.getReviewStatus() != null && r.getReviewStatus() == 1 && r.getRating() == 5)
            .count();
        
        return counts;
    }*/

    private void validateMusicalExists(Long musicalId) {
        // 不再抛出异常，而是允许自动创建缺失的Musical
        // 由getMusical方法处理缺失情况
    }

    private Review getReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("评价不存在: " + reviewId));
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在: " + username));
    }

    private Musical getMusical(Long musicalId) {
        return musicalRepository.findById(musicalId)
                .orElseThrow(() -> new ResourceNotFoundException("剧目不存在: " + musicalId));
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