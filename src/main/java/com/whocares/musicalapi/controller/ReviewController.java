package com.whocares.musicalapi.controller;

import com.whocares.musicalapi.dto.request.ReviewRequest;
import com.whocares.musicalapi.dto.request.ReviewStatusRequest;
import com.whocares.musicalapi.dto.response.ReviewResponse;
import com.whocares.musicalapi.dto.response.ReviewStatisticsResponse;
import com.whocares.musicalapi.entity.Review;
import com.whocares.musicalapi.service.ReviewService;
import com.whocares.musicalapi.service.impl.ReviewServiceImpl;
import jakarta.validation.Valid;
import com.whocares.musicalapi.security.jwt.JwtTokenProvider;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReviewController {

    @Autowired
    private final ReviewServiceImpl reviewService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = (ReviewServiceImpl) reviewService;
    }

    private String getUsernameFromToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                return jwtTokenProvider.getUsernameFromToken(token);
            } catch (Exception e) {
                // 令牌无效或过期，返回匿名用户
                return "anonymous";
            }
        }
        return "anonymous"; // 没有令牌或令牌格式不正确
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getPrincipal().equals("anonymousUser")) {
            return authentication.getName();
        }
        return "anonymous";
    }

    /*@GetMapping("/reviews")
    public List<Review> getAllReviews() {
        return reviewService.getAllReviews();
    }*/

    //按状态分类获取所有评价
    @GetMapping("/by-status")
    public ResponseEntity<Page<Review>> getReviewsByStatus(
            @RequestParam Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        // 创建Pageable对象
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Order.desc("createdAt")) // 按创建时间降序
        );

        return ResponseEntity.ok(reviewService.findByReviewStatus(status, pageable));
    }
    //return reviewService.getReviewsByStatus(status);

    //}

    // 获取某个剧目的所有评价 (分页)
    @GetMapping("/musical/{musicalId}")
    public ResponseEntity<Page<ReviewResponse>> getReviewsByMusical(
            @PathVariable Long musicalId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer rating) {
        return ResponseEntity.ok(reviewService.getReviewsByMusical(musicalId, page, size, rating));
    }

    // 获取用户的所有评价
    @GetMapping("/user/me")
    public ResponseEntity<Page<ReviewResponse>> getMyReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String username = getCurrentUsername();
        return ResponseEntity.ok(reviewService.getReviewsByUser(username, page, size));
    }

    // 获取某个剧目的评价统计信息
    @GetMapping("/musical/{musicalId}/statistics")
    public ResponseEntity<ReviewStatisticsResponse> getReviewStatistics(@PathVariable Long musicalId) {
        return ResponseEntity.ok(reviewService.getReviewStatistics(musicalId));
    }

    // 创建新评价
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @Valid @RequestBody ReviewRequest reviewRequest,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String username = getCurrentUsername();
        return ResponseEntity.ok(reviewService.createReview(reviewRequest, username));
    }

    // 更新评价
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequest reviewRequest) {
        String username = getCurrentUsername();
        return ResponseEntity.ok(reviewService.updateReview(reviewId, reviewRequest, username));
    }

    // 更新评价状态
    @PatchMapping("/{reviewId}/status")
    public ResponseEntity<ReviewResponse> updateReviewStatus(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewStatusRequest statusRequest) {
        String username = getCurrentUsername();
        return ResponseEntity.ok(reviewService.updateReviewStatus(reviewId, statusRequest.getStatus(), username));
    }

    // 删除评价
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        String username = getCurrentUsername();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        reviewService.deleteReview(reviewId, username, authentication.getAuthorities());
        return ResponseEntity.noContent().build();
    }

    // 检查用户是否已对某个剧目评价过
    @GetMapping("/check/{musicalId}")
    public ResponseEntity<Boolean> hasUserReviewed(
            @PathVariable Long musicalId) {
        String username = getCurrentUsername();
        if (username.equals("anonymous")) {
            return ResponseEntity.ok(false); // 匿名用户始终可以评论
        }
        return ResponseEntity.ok(reviewService.hasUserReviewed(username, musicalId));
    }
}
