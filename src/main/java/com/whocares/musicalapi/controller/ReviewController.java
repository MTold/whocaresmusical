package com.whocares.musicalapi.controller;

import com.whocares.musicalapi.dto.request.ReviewRequest;
import com.whocares.musicalapi.dto.response.ReviewResponse;
import com.whocares.musicalapi.dto.response.ReviewStatisticsResponse;
import com.whocares.musicalapi.entity.Review;
import com.whocares.musicalapi.service.ReviewService;
import com.whocares.musicalapi.service.impl.ReviewServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReviewController {

    @Autowired
    private final ReviewServiceImpl reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = (ReviewServiceImpl) reviewService;
    }

    /*@GetMapping("/reviews")
    public List<Review> getAllReviews() {
        return reviewService.getAllReviews();
    }*/
    @GetMapping("/by-status")
    public List<Review> getReviewsByStatus(@RequestParam(required = false) Integer status) {
        if (status == null) {
            return reviewService.getAllReviews();
        } else {
            return reviewService.getReviewsByStatus(status);
        }
    }


    // 获取某个剧目的所有评价 (分页)
    @GetMapping("/performance/{performanceId}")
    public ResponseEntity<Page<ReviewResponse>> getReviewsByPerformance(
            @PathVariable Long performanceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer rating) {
        return ResponseEntity.ok(reviewService.getReviewsByPerformance(performanceId, page, size, rating));
    }

    // 获取用户的所有评价
    @GetMapping("/user/me")
    // @PreAuthorize("hasRole('USER')") - 暂时禁用登录要求
    public ResponseEntity<Page<ReviewResponse>> getMyReviews(
            //@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(reviewService.getReviewsByUser("anonymous", page, size));
    }

    // 获取某个剧目的评价统计信息
    @GetMapping("/performance/{performanceId}/statistics")
    public ResponseEntity<ReviewStatisticsResponse> getReviewStatistics(@PathVariable Long performanceId) {
        return ResponseEntity.ok(reviewService.getReviewStatistics(performanceId));
    }

    // 创建新评价
    @PostMapping
    // @PreAuthorize("hasRole('USER')") - 暂时禁用登录要求
    public ResponseEntity<ReviewResponse> createReview(
            @Valid @RequestBody ReviewRequest reviewRequest) {
        // 暂时使用匿名用户
        return ResponseEntity.ok(reviewService.createReview(reviewRequest, "anonymous"));
    }

    // 更新评价
    @PutMapping("/{reviewId}")
    // @PreAuthorize("hasRole('USER')") - 暂时禁用登录要求，允许任何人更新
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequest reviewRequest) {
        // 暂时允许匿名用户更新任何评价
        return ResponseEntity.ok(reviewService.updateReview(reviewId, reviewRequest, "anonymous"));
    }

    // 删除评价
    @DeleteMapping("/{reviewId}")
    // @PreAuthorize("hasRole('USER') or hasRole('ADMIN')") - 暂时禁用登录要求
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        // 暂时允许匿名用户删除任何评价
        reviewService.deleteReview(reviewId, "anonymous", null);
        return ResponseEntity.noContent().build();
    }

    // 检查用户是否已对某个剧目评价过
    @GetMapping("/check/{performanceId}")
    // @PreAuthorize("hasRole('USER')") - 暂时禁用登录要求
    public ResponseEntity<Boolean> hasUserReviewed(@PathVariable Long performanceId) {
        // 匿名的用户永远返回false，让他们始终可以评论
        return ResponseEntity.ok(false);
    }
}
