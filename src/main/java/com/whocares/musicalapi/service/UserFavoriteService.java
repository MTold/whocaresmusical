package com.whocares.musicalapi.service;

import com.whocares.musicalapi.dto.response.FavoriteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserFavoriteService {
    
    /**
     * 添加收藏
     */
    void addFavorite(String username, Long musicalId);
    
    /**
     * 取消收藏
     */
    void removeFavorite(String username, Long musicalId);
    
    /**
     * 获取用户的收藏列表
     */
    Page<FavoriteResponse> getUserFavorites(String username, int page, int size);
    
    /**
     * 检查用户是否已收藏某个剧目
     */
    boolean isFavorite(String username, Long musicalId);
    
    /**
     * 获取用户的收藏数量
     */
    Long getFavoriteCount(String username);
}