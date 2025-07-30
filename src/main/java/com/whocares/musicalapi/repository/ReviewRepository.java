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
    @Query(value = """
    SELECT
        COUNT(*)                    AS total_count,
        COALESCE(AVG(r.rating), 0)  AS average_rating,
        SUM(CASE WHEN r.rating = 1 THEN 1 ELSE 0 END) AS rating1_count,
        SUM(CASE WHEN r.rating = 2 THEN 1 ELSE 0 END) AS rating2_count,
        SUM(CASE WHEN r.rating = 3 THEN 1 ELSE 0 END) AS rating3_count,
        SUM(CASE WHEN r.rating = 4 THEN 1 ELSE 0 END) AS rating4_count,
        SUM(CASE WHEN r.rating = 5 THEN 1 ELSE 0 END) AS rating5_count
    FROM reviews r
    WHERE r.musical_id = :musicalId
      AND r.review_status = 1
    """, nativeQuery = true)

    //@Query("SELECT COUNT(r), AVG(r.rating) FROM Review r WHERE r.musical.id = :musicalId")
    List<Object[]> getReviewStatistics(@Param("musicalId") Long musicalId);
    
    // 获取所有评价（管理后台用）
    Page<Review> findAllByOrderByCreatedAtDesc(Pageable pageable);
    // 根据评价状态查询评价
        // 修改后的分页查询方法
        Page<Review> findByReviewStatus(Integer reviewStatus, Pageable pageable);

        // 可选：添加关联查询（如果需要剧目信息）
        @Query("SELECT r FROM Review r LEFT JOIN FETCH r.musical WHERE r.reviewStatus = :status")
        Page<Review> findByStatusWithMusical(@Param("status") Integer status, Pageable pageable);

    // 根据评分筛选评价
    Page<Review> findByMusicalIdAndRatingOrderByCreatedAtDesc(Long musicalId, Integer rating, Pageable pageable);

    // 计算某部剧目的平均评分（只统计已通过的评价）
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.musical.id = :musicalId AND r.reviewStatus = 1")
    Double calculateAverageRatingByMusicalId(@Param("musicalId") Long musicalId);

    // 获取所有有评分的剧目及其平均评分
    @Query("SELECT r.musical.id, AVG(r.rating) as avgRating FROM Review r WHERE r.reviewStatus = 1 GROUP BY r.musical.id HAVING AVG(r.rating) > 0 ORDER BY AVG(r.rating) DESC")
    List<Object[]> getMusicalIdsWithAverageRatings();
}