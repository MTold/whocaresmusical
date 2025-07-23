package com.whocares.musicalapi.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class BrowsingHistoryResponse {
    private Long id;
    private Long musicalId;
    private String musicalName;
    private String musicalImage;
    private String musicalDescription;
    private String musicalVenue;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime viewedAt;
    
    public BrowsingHistoryResponse() {}
    
    public BrowsingHistoryResponse(Long id, Long musicalId, String musicalName, String musicalImage, 
                                 String musicalDescription, String musicalVenue, LocalDateTime viewedAt) {
        this.id = id;
        this.musicalId = musicalId;
        this.musicalName = musicalName;
        this.musicalImage = musicalImage;
        this.musicalDescription = musicalDescription;
        this.musicalVenue = musicalVenue;
        this.viewedAt = viewedAt;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getMusicalId() {
        return musicalId;
    }
    
    public void setMusicalId(Long musicalId) {
        this.musicalId = musicalId;
    }
    
    public String getMusicalName() {
        return musicalName;
    }
    
    public void setMusicalName(String musicalName) {
        this.musicalName = musicalName;
    }
    
    public String getMusicalImage() {
        return musicalImage;
    }
    
    public void setMusicalImage(String musicalImage) {
        this.musicalImage = musicalImage;
    }
    
    public String getMusicalDescription() {
        return musicalDescription;
    }
    
    public void setMusicalDescription(String musicalDescription) {
        this.musicalDescription = musicalDescription;
    }
    
    public String getMusicalVenue() {
        return musicalVenue;
    }
    
    public void setMusicalVenue(String musicalVenue) {
        this.musicalVenue = musicalVenue;
    }
    
    public LocalDateTime getViewedAt() {
        return viewedAt;
    }
    
    public void setViewedAt(LocalDateTime viewedAt) {
        this.viewedAt = viewedAt;
    }
}