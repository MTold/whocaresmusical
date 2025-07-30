package com.whocares.musicalapi.service;

import com.whocares.musicalapi.dto.request.ShopReviewRequest;
import com.whocares.musicalapi.dto.response.ShopReviewResponse;
import com.whocares.musicalapi.entity.ShopReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public interface ShopReviewService {


    List<ShopReviewResponse> getShopReviewsByShop(Long shopId);

    List<ShopReviewResponse> getShopReviewsByUser(String username);

    // 添加新的方法来按状态分类获取所有评价
    //List<ShopReviewResponse> getShopReviewsByStatus(Integer status, Pageable pageable);


    Page<ShopReview> findByReviewStatus(Integer status, Pageable pageable);

    ShopReviewResponse createShopReview(ShopReviewRequest shopReviewRequest, String username);

    ShopReviewResponse updateShopReview(Long shopReviewId, ShopReviewRequest shopReviewRequest, String username);

    ShopReviewResponse updateReviewStatus(Long shopReviewId, Integer status, String username);

    void deleteShopReview(Long shopReviewId, String username, Iterable<? extends GrantedAuthority> authorities);

    //先不使用，同一用户可以对同一店铺发布多次评价
    //boolean hasUserReviewed(String username, Long shopId);

}
