package com.whocares.musicalapi.repository;

import com.whocares.musicalapi.entity.UserFavorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFavoriteRepository extends JpaRepository<UserFavorite, Long> {
    
    // 查找用户的收藏列表
    Page<UserFavorite> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    // 检查用户是否已收藏某个剧目
    Optional<UserFavorite> findByUserIdAndMusicalId(Long userId, Long musicalId);
    
    // 删除用户的某个收藏
    void deleteByUserIdAndMusicalId(Long userId, Long musicalId);
    
    // 获取用户的收藏数量
    Long countByUserId(Long userId);
    
    // 获取收藏某个剧目的用户数量
    Long countByMusicalId(Long musicalId);
    
    // 检查用户是否已收藏某个剧目（返回布尔值）
    boolean existsByUserIdAndMusicalId(Long userId, Long musicalId);
}