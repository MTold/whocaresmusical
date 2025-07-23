package com.whocares.musicalapi.controller;

import com.whocares.musicalapi.dto.response.FavoriteResponse;
import com.whocares.musicalapi.service.UserFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
public class UserFavoriteController {

    private final UserFavoriteService userFavoriteService;

    @Autowired
    public UserFavoriteController(UserFavoriteService userFavoriteService) {
        this.userFavoriteService = userFavoriteService;
    }

    /**
     * 添加收藏
     */
    @PostMapping("/{musicalId}")
    public ResponseEntity<Void> addFavorite(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long musicalId) {
        
        userFavoriteService.addFavorite(userDetails.getUsername(), musicalId);
        return ResponseEntity.ok().build();
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/{musicalId}")
    public ResponseEntity<Void> removeFavorite(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long musicalId) {
        
        userFavoriteService.removeFavorite(userDetails.getUsername(), musicalId);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取用户的收藏列表
     */
    @GetMapping
    public ResponseEntity<Page<FavoriteResponse>> getUserFavorites(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<FavoriteResponse> favorites = userFavoriteService.getUserFavorites(
            userDetails.getUsername(), page, size);
        return ResponseEntity.ok(favorites);
    }

    /**
     * 检查用户是否已收藏某个剧目
     */
    @GetMapping("/{musicalId}/status")
    public ResponseEntity<Boolean> isFavorite(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long musicalId) {
        
        boolean isFavorite = userFavoriteService.isFavorite(
            userDetails.getUsername(), musicalId);
        return ResponseEntity.ok(isFavorite);
    }

    /**
     * 获取用户的收藏数量
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getFavoriteCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long count = userFavoriteService.getFavoriteCount(userDetails.getUsername());
        return ResponseEntity.ok(count);
    }
}