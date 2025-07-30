package com.whocares.musicalapi.service;

import com.whocares.musicalapi.dto.response.RecommendationResponse;
import com.whocares.musicalapi.entity.Musical;
import java.util.List;

public interface RecommendationService {
    
    /**
     * Get personalized recommendations for a user
     * @param userId the user ID
     * @return list of recommended musicals
     */
    List<RecommendationResponse> getPersonalizedRecommendations(Long userId);
    
    /**
     * Get related musicals based on a specific musical
     * @param musicalId the musical ID
     * @return list of related musicals
     */
    List<RecommendationResponse> getRelatedMusicals(Long musicalId);
    
    /**
     * Get popular musicals across all users
     * @return list of popular musicals
     */
    List<RecommendationResponse> getPopularMusicals();
}