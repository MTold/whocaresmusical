package com.whocares.musicalapi.service.impl;

import com.whocares.musicalapi.dto.response.RecommendationResponse;
import com.whocares.musicalapi.entity.Musical;
import com.whocares.musicalapi.entity.Review;
import com.whocares.musicalapi.entity.UserBrowsingHistory;
import com.whocares.musicalapi.entity.UserFavorite;
import com.whocares.musicalapi.repository.MusicalRepository;
import com.whocares.musicalapi.repository.ReviewRepository;
import com.whocares.musicalapi.repository.UserBrowsingHistoryRepository;
import com.whocares.musicalapi.repository.UserFavoriteRepository;
import com.whocares.musicalapi.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {
    
    @Autowired
    private MusicalRepository musicalRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private UserBrowsingHistoryRepository browsingHistoryRepository;
    
    @Autowired
    private UserFavoriteRepository favoriteRepository;
    
    @Override
    public List<RecommendationResponse> getPersonalizedRecommendations(Long userId) {
        // Get user's browsing history
        List<UserBrowsingHistory> browsingHistory = browsingHistoryRepository.findByUserUserIdOrderByViewedAtDesc(userId, org.springframework.data.domain.Pageable.unpaged()).getContent();
        
        // Get user's favorites
        List<UserFavorite> favorites = favoriteRepository.findByUserIdOrderByCreatedAtDesc(userId, org.springframework.data.domain.Pageable.unpaged()).getContent();
        
        // Get user's reviews
        List<Review> reviews = reviewRepository.findByUserIdOrderByCreatedAtDesc(userId, org.springframework.data.domain.Pageable.unpaged()).getContent();
        
        // Collect musical IDs from user's interactions
        Set<Long> userMusicalIds = new HashSet<>();
        
        // Add musicals from browsing history
        browsingHistory.forEach(history -> userMusicalIds.add(history.getMusical().getId()));
        
        // Add musicals from favorites
        favorites.forEach(favorite -> userMusicalIds.add(favorite.getMusical().getId()));
        
        // Add musicals from reviews
        reviews.forEach(review -> userMusicalIds.add(review.getMusical().getId()));
        
        // If no user history, return popular musicals
        if (userMusicalIds.isEmpty()) {
            return convertToRecommendationResponse(getPopularMusicalsEntity());
        }
        
        // Find similar musicals based on user's history
        List<Musical> allMusicals = musicalRepository.findAll();
        Map<Musical, Double> similarityScores = new HashMap<>();
        
        // Calculate similarity scores for each musical
        for (Musical musical : allMusicals) {
            if (userMusicalIds.contains(musical.getId())) {
                continue; // Skip musicals the user has already interacted with
            }
            
            double score = calculateSimilarity(musical, userMusicalIds, browsingHistory, favorites, reviews);
            if (score > 0) {
                similarityScores.put(musical, score);
            }
        }
        
        // Sort by similarity score and return top recommendations
        List<Musical> recommendedMusicals = similarityScores.entrySet().stream()
                .sorted(Map.Entry.<Musical, Double>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        
        return convertToRecommendationResponse(recommendedMusicals);
    }
    
    @Override
    public List<RecommendationResponse> getRelatedMusicals(Long musicalId) {
        // Get the reference musical
        Optional<Musical> referenceMusicalOpt = musicalRepository.findById(musicalId);
        if (!referenceMusicalOpt.isPresent()) {
            return new ArrayList<>();
        }
        
        Musical referenceMusical = referenceMusicalOpt.get();
        
        // Get all musicals
        List<Musical> allMusicals = musicalRepository.findAll();
        
        // Calculate similarity scores
        Map<Musical, Double> similarityScores = new HashMap<>();
        
        for (Musical musical : allMusicals) {
            if (musical.getId().equals(musicalId)) {
                continue; // Skip the reference musical itself
            }
            
            double score = calculateGenreSimilarity(referenceMusical, musical);
            if (score > 0) {
                similarityScores.put(musical, score);
            }
        }
        
        // Sort by similarity score and return top recommendations
        List<Musical> relatedMusicals = similarityScores.entrySet().stream()
                .sorted(Map.Entry.<Musical, Double>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        
        return convertToRecommendationResponse(relatedMusicals);
    }
    
    @Override
    public List<RecommendationResponse> getPopularMusicals() {
        // Get all musicals ordered by average rating
        List<Musical> popularMusicals = musicalRepository.findAllByOrderByAverageRatingDesc().orElse(new ArrayList<>());
        return convertToRecommendationResponse(popularMusicals);
    }
    
    /**
     * Get popular musicals as entity objects (used internally)
     */
    private List<Musical> getPopularMusicalsEntity() {
        // Get all musicals ordered by average rating
        return musicalRepository.findAllByOrderByAverageRatingDesc().orElse(new ArrayList<>());
    }
    
    /**
     * Convert Musical entities to RecommendationResponse DTOs
     */
    private List<RecommendationResponse> convertToRecommendationResponse(List<Musical> musicals) {
        return musicals.stream().map(musical -> {
            RecommendationResponse response = new RecommendationResponse();
            response.setId(musical.getId());
            response.setName(musical.getName());
            response.setDescription(musical.getInfo());
            response.setGenre(musical.getGenre());
            response.setAverageRating(musical.getAverageRating() != null ? 
                BigDecimal.valueOf(musical.getAverageRating()) : null);
            response.setImageUrl(musical.getImageUrl());
            // Note: reviewCount would need to be fetched from ReviewRepository
            // For now, we'll set it to 0 as a placeholder
            response.setReviewCount(0);
            return response;
        }).collect(Collectors.toList());
    }
    
    /**
     * Calculate similarity between a musical and user's preferences
     */
    private double calculateSimilarity(Musical musical, Set<Long> userMusicalIds, 
                                    List<UserBrowsingHistory> browsingHistory,
                                    List<UserFavorite> favorites,
                                    List<Review> reviews) {
        double score = 0.0;
        
        // Weight factors
        double browsingWeight = 0.3;
        double favoriteWeight = 0.4;
        double reviewWeight = 0.3;
        
        // Check if user has browsed this musical's genre
        long browsingMatches = browsingHistory.stream()
                .filter(history -> history.getMusical().getGenre().equals(musical.getGenre()))
                .count();
        score += browsingMatches * browsingWeight;
        
        // Check if user has favorited this musical's genre
        long favoriteMatches = favorites.stream()
                .filter(favorite -> favorite.getMusical().getGenre().equals(musical.getGenre()))
                .count();
        score += favoriteMatches * favoriteWeight;
        
        // Check if user has reviewed similar genres
        long reviewMatches = reviews.stream()
                .filter(review -> review.getMusical().getGenre().equals(musical.getGenre()))
                .count();
        score += reviewMatches * reviewWeight;
        
        // Boost score for high-rated musicals
        if (musical.getAverageRating() != null && musical.getAverageRating() >= 4.0) {
            score *= 1.2;
        }
        
        return score;
    }
    
    /**
     * Calculate genre similarity between two musicals
     */
    private double calculateGenreSimilarity(Musical musical1, Musical musical2) {
        if (musical1.getGenre() == null || musical2.getGenre() == null) {
            return 0.0;
        }
        
        // Exact genre match
        if (musical1.getGenre().equals(musical2.getGenre())) {
            return 1.0;
        }
        
        // Partial match (e.g., "音乐剧" and "音乐剧-爱情")
        if (musical1.getGenre().contains(musical2.getGenre()) || 
            musical2.getGenre().contains(musical1.getGenre())) {
            return 0.7;
        }
        
        return 0.0;
    }
}