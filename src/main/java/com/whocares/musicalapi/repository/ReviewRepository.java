package com.whocares.musicalapi.repository;

import com.whocares.musicalapi.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    // 根据剧目ID查找评价
    Page<Review> findByMusicalIdOrderByCreatedAtDesc(Long musicalId, Pageable pageable);
    
    // 根据用户ID查找评价
    Page<Review> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    // 获取用户的某个评价（用于检查是否已评价过）
    Review findByUserIdAndMusicalId(Long userId, Long musicalId);
    
    // 获取剧目的评轮平均分和数量
    @Query("SELECT COUNT(r), AVG(r.rating) FROM Review r WHERE r.musical.id = :musicalId")
    List<Object[]> getReviewStatistics(@Param("musicalId") Long musicalId);
    
    // 获取所有评价（管理后台用）
    Page<Review> findAllByOrderByCreatedAtDesc(Pageable pageable);
    // 根据评价状态查询评价
    List<Review> findByReviewStatus(Integer reviewStatus);
    // 根据评分筛选评价
    Page<Review> findByMusicalIdAndRatingOrderByCreatedAtDesc(Long musicalId, Integer rating, Pageable pageable);
}