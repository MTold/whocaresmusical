package com.whocares.musicalapi.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RecommendationResponse {
    private Long id;
    private String name;
    private String description;
    private String genre;
    private BigDecimal averageRating;
    private String imageUrl;
    private Integer reviewCount;
    
    // Constructors
    public RecommendationResponse() {}
    
    public RecommendationResponse(Long id, String name, String description, String genre, 
                                BigDecimal averageRating, String imageUrl, Integer reviewCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.genre = genre;
        this.averageRating = averageRating;
        this.imageUrl = imageUrl;
        this.reviewCount = reviewCount;
    }
}