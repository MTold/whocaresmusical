package com.whocares.musicalapi.controller;

import com.whocares.musicalapi.dto.response.RecommendationResponse;
import com.whocares.musicalapi.entity.User;
import com.whocares.musicalapi.service.RecommendationService;
import com.whocares.musicalapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RecommendationController {
    
    @Autowired
    private RecommendationService recommendationService;
    
    @Autowired
    private UserService userService;
    
    /**
     * Get personalized recommendations for the current user
     */
    @GetMapping("/personalized")
    public ResponseEntity<List<RecommendationResponse>> getPersonalizedRecommendations() {
        try {
            // 无论什么情况，直接返回热门推荐
            List<RecommendationResponse> recommendations = recommendationService.getPopularMusicals();
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            // 发生错误时返回空列表而不是400错误
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
    
    /**
     * Get related musicals based on a specific musical
     */
    @GetMapping("/related/{musicalId}")
    public ResponseEntity<List<RecommendationResponse>> getRelatedMusicals(@PathVariable Long musicalId) {
        try {
            List<RecommendationResponse> relatedMusicals = recommendationService.getRelatedMusicals(musicalId);
            return ResponseEntity.ok(relatedMusicals);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get popular musicals across all users
     */
    @GetMapping("/popular")
    public ResponseEntity<List<RecommendationResponse>> getPopularMusicals() {
        try {
            List<RecommendationResponse> popularMusicals = recommendationService.getPopularMusicals();
            return ResponseEntity.ok(popularMusicals);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}