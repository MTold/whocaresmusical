package com.whocares.musicalapi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_browsing_history")
public class UserBrowsingHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "musical_id", nullable = false)
    private Musical musical;
    
    @Column(name = "viewed_at", nullable = false)
    private LocalDateTime viewedAt;
    
    public UserBrowsingHistory() {}
    
    public UserBrowsingHistory(User user, Musical musical) {
        this.user = user;
        this.musical = musical;
        this.viewedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Musical getMusical() {
        return musical;
    }
    
    public void setMusical(Musical musical) {
        this.musical = musical;
    }
    
    public LocalDateTime getViewedAt() {
        return viewedAt;
    }
    
    public void setViewedAt(LocalDateTime viewedAt) {
        this.viewedAt = viewedAt;
    }
    
    @PrePersist
    public void prePersist() {
        if (viewedAt == null) {
            viewedAt = java.time.LocalDateTime.now();
        }
    }
}