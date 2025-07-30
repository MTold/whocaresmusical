package com.whocares.musicalapi.controller;

import com.whocares.musicalapi.dto.request.ReviewRequest;
import com.whocares.musicalapi.dto.request.ReviewStatusRequest;
import com.whocares.musicalapi.dto.request.ShopReviewRequest;
import com.whocares.musicalapi.dto.response.ReviewResponse;
import com.whocares.musicalapi.dto.response.ShopReviewResponse;
import com.whocares.musicalapi.entity.Review;
import com.whocares.musicalapi.entity.ShopReview;
import com.whocares.musicalapi.security.jwt.JwtTokenProvider;
import com.whocares.musicalapi.service.ShopReviewService;
import com.whocares.musicalapi.service.impl.ShopReviewServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shopReviews")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ShopReviewController {

    @Autowired
    private final ShopReviewService shopReviewService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public ShopReviewController(ShopReviewService shopReviewService) {
        this.shopReviewService = shopReviewService;
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

    /*@GetMapping("/shopReviews")
    public List<ShopReview> getAllShopReviews() {
        return shopReviewService.getAllShopReviews();
    }*/

    //按状态分类获取所有评价
    @GetMapping("/by-status")
    public ResponseEntity<Page<ShopReview>> getShopReviewsByStatus(
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

        return ResponseEntity.ok(shopReviewService.findByReviewStatus(status, pageable));
    }

    // 获取某个店铺的所有评价 (分页)
    @GetMapping("/shop/{shopId}")
    public Map<String, Object> getShopReviewsByShop(@PathVariable Long shopId) {
        List<ShopReviewResponse> shopReviews = shopReviewService.getShopReviewsByShop(shopId);
        Map<String, Object> response = new HashMap<>();
        response.put("data", shopReviews);
        response.put("code", 200); // 或者根据你的业务逻辑定义合适的code
        response.put("msg", "success"); //
        return response;
    }

    // 获取用户的所有店铺评价
    @GetMapping("/user/me")
    public ResponseEntity<List<ShopReviewResponse>> getMyShopReviews() {
        String username = getCurrentUsername();
        return ResponseEntity.ok(shopReviewService.getShopReviewsByUser(username));
    }

    // 创建新店铺评价
    @PostMapping
    public ResponseEntity<ShopReviewResponse> createShopReview(
            @Valid @RequestBody ShopReviewRequest shopReviewRequest,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String username = getCurrentUsername();
        return ResponseEntity.ok(shopReviewService.createShopReview(shopReviewRequest, username));
    }

    // 更新店铺评价
    @PutMapping("/{shopReviewId}")
    public ResponseEntity<ShopReviewResponse> updateShopReview(
            @PathVariable Long shopReviewId,
            @Valid @RequestBody ShopReviewRequest shopReviewRequest) {
        String username = getCurrentUsername();
        return ResponseEntity.ok(shopReviewService.updateShopReview(shopReviewId, shopReviewRequest, username));
    }
    // 更新评价状态
    @PatchMapping("/{shopReviewId}/status")
    public ResponseEntity<ShopReviewResponse> updateReviewStatus(
            @PathVariable Long shopReviewId,
            @Valid @RequestBody ReviewStatusRequest statusRequest) {
        String username = getCurrentUsername();
        return ResponseEntity.ok(shopReviewService.updateReviewStatus(shopReviewId, statusRequest.getStatus(), username));
    }
    // 删除评价
    @DeleteMapping("/{shopReviewId}")
    public ResponseEntity<Void> deleteShopReview(@PathVariable Long shopReviewId) {
        String username = getCurrentUsername();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        shopReviewService.deleteShopReview(shopReviewId, username, authentication.getAuthorities());
        return ResponseEntity.noContent().build();
    }

}
