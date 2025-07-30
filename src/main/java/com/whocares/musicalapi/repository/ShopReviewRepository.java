package com.whocares.musicalapi.repository;

import com.whocares.musicalapi.entity.Review;
import com.whocares.musicalapi.entity.ShopReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopReviewRepository extends JpaRepository<ShopReview, Long> {
    // 根据店铺名称查找评价
    List<ShopReview> findByShopIdOrderByCreatedAtDesc(Long shopId);

    // 根据用户ID查找评价
    List<ShopReview> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 获取用户的某个评价（用于检查是否已评价过）
    ShopReview findByUserIdAndShopId(Long userId, Long shopId);

    // 获取所有评价（管理后台用）
    Page<ShopReview> findAllByOrderByCreatedAtDesc(Pageable pageable);

    //根据评价状态查询评价
    Page<ShopReview> findByReviewStatus(Integer reviewStatus, Pageable pageable);

}
