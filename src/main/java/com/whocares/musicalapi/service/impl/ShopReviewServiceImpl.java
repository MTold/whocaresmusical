package com.whocares.musicalapi.service.impl;

import com.whocares.musicalapi.dto.request.ShopReviewRequest;
import com.whocares.musicalapi.dto.response.ReviewResponse;
import com.whocares.musicalapi.dto.response.ShopReviewResponse;
import com.whocares.musicalapi.entity.*;
import com.whocares.musicalapi.exception.ResourceNotFoundException;
import com.whocares.musicalapi.repository.*;
import com.whocares.musicalapi.service.ReviewService;
import com.whocares.musicalapi.service.ShopReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ShopReviewServiceImpl implements ShopReviewService {

    @Autowired
    private final ShopReviewRepository shopReviewRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;

    public ShopReviewServiceImpl(ShopReviewRepository shopReviewRepository, UserRepository userRepository, ShopRepository shopRepository) {
        this.shopReviewRepository = shopReviewRepository;
        this.userRepository = userRepository;
        this.shopRepository = shopRepository;
    }

    //根据店铺查找评价
    @Override
    @Transactional(readOnly = true)
    public List<ShopReviewResponse> getShopReviewsByShop(Long shopId) {
        validateShopExists(shopId);
        List<ShopReview> reviews;
        reviews = shopReviewRepository.findByShopIdOrderByCreatedAtDesc(shopId);
        List<ShopReviewResponse> responses = reviews.stream()  // 创建一个流
                .map(this::convertToResponse)  // 将每个 Review 转换为 ReviewResponse
                .collect(Collectors.toList());  // 将结果收集到一个新的列表中
        return responses;
    }

    //根据用户查找店铺评价
    @Override
    @Transactional(readOnly = true)
    public List<ShopReviewResponse> getShopReviewsByUser(String username) {
        User user = getUserByUsername(username);
        List<ShopReview> reviews = shopReviewRepository.findByUserIdOrderByCreatedAtDesc(user.getUserId());
        List<ShopReviewResponse> responses = reviews.stream()  // 创建一个流
                .map(this::convertToResponse)  // 将每个 Review 转换为 ReviewResponse
                .collect(Collectors.toList());  // 将结果收集到一个新的列表中
        return responses;
    }

    //根据评价状态查找店铺评价
    @Override
    public Page<ShopReview> findByReviewStatus(Integer status, Pageable pageable) {
        return shopReviewRepository.findByReviewStatus(status, pageable);
    }

    @Override
    public ShopReviewResponse createShopReview(ShopReviewRequest shopReviewRequest, String username) {
        // 如果是匿名用户，不查找User实体，直接创建评论
        Shop shop = getShop(shopReviewRequest.getShopId());

        ShopReview shopReview = new ShopReview();
        shopReview.setContent(shopReviewRequest.getContent());
        shopReview.setReviewStatus(1); // 默认状态为正常

        // 针对匿名用户，设置user为null，让数据库user_id为null
        if (!username.equals("anonymous")) {
            User user = getUserByUsername(username);
            shopReview.setUser(user);
            // 检查是否已经评价过
            /*if (hasUserReviewed(username, shopReviewRequest.getShopId())) {
                throw new IllegalStateException("用户已经对该剧目评价过");
            }*/
        } else {
            // 匿名用户直接创建评论，不做重复检查
            shopReview.setUser(null);
        }
        shopReview.setShop(shop);

        shopReview = shopReviewRepository.save(shopReview);
        return convertToResponse(shopReview);
    }

    @Override
    public ShopReviewResponse updateShopReview(Long shopReviewId, ShopReviewRequest shopReviewRequest, String username) {
        ShopReview shopReview = getShopReview(shopReviewId);

        // 对于已登录用户的验证逻辑（暂时禁用）
        if (!username.equals("anonymous")) {
            User user = getUserByUsername(username);
            // 验证是否是评价所有者 - 暂时禁用
            if (shopReview.getUser() != null && !shopReview.getUser().getUserId().equals(user.getUserId())) {
                throw new IllegalStateException("只能修改自己的评价");
            }
        }
        // 匿名用户允许更新任何评价（临时方案）

        // 验证是否还是同一个店铺（不允许更改评价的店铺）
        if (!shopReview.getShop().getId().equals(shopReviewRequest.getShopId())) {
            throw new IllegalArgumentException("不能更改评价的店铺 ");
        }

        shopReview.setContent(shopReviewRequest.getContent());

        shopReview = shopReviewRepository.save(shopReview);
        return convertToResponse(shopReview);
    }

    @Override
    public ShopReviewResponse updateReviewStatus(Long shopReviewId, Integer status, String username) {
        ShopReview shopReview = getShopReview(shopReviewId);
        shopReview.setReviewStatus(status);
        shopReview = shopReviewRepository.save(shopReview);
        return convertToResponse(shopReview);
    }

    @Override
    public void deleteShopReview(Long shopReviewId, String username, Iterable<? extends GrantedAuthority> authorities) {
        ShopReview shopReview = getShopReview(shopReviewId);

        // 对于已登录用户的验证逻辑（暂时禁用）
        if (!username.equals("anonymous")) {
            User user = getUserByUsername(username);

            // 检查是否是评价所有者或管理员 - 暂时禁用或简化
            if (shopReview.getUser() != null && !shopReview.getUser().getUserId().equals(user.getUserId())) {
                boolean isAdmin = false;
                for (GrantedAuthority authority : authorities) {
                    if (authority.getAuthority().equals("ROLE_ADMIN")) {
                        isAdmin = true;
                        break;
                    }
                }
                if (!isAdmin) {
                    throw new IllegalStateException("只能删除自己的评价或管理员删除");
                }
            }
        }
        // 匿名用户允许删除任何评价（临时方案）

        shopReviewRepository.delete(shopReview);
    }

    //先不使用，同一用户可以对同一店铺发布多次评价
    /*@Override
    public boolean hasUserReviewed(String username, Long shopId) {
        if (username == null || username.equals("anonymous")) {
            return false; // 匿名用户或未登录用户
        }
        User user = getUserByUsername(username);
        ShopReview existingReview = shopReviewRepository.findByUserIdAndShopId(user.getUserId(), shopId);
        return existingReview != null;
    }*/

    private ShopReviewResponse convertToResponse(ShopReview shopReview) {
        ShopReviewResponse response = new ShopReviewResponse();

        // Safe field mapping with null checks
        if (shopReview != null) {
            response.setId(shopReview.getId());
            response.setContent(shopReview.getContent());
            response.setCreatedAt(shopReview.getCreatedAt());
            response.setShopId(shopReview.getShop() != null ? shopReview.getShop().getId() : null);
            response.setStatus(shopReview.getReviewStatus()); // 添加状态字段

            if (shopReview.getUser() != null) {
                response.setUserId(shopReview.getUser().getUserId());
                response.setUsername(shopReview.getUser().getUsername());
                response.setUserImage(shopReview.getUser().getUserImage());
            } else {
                // 匿名用户的情况
                response.setUserId(null);
                response.setUsername("匿名用户");
                response.setUserImage(null);
            }

            if (shopReview.getShop() != null) {
                response.setShopName(shopReview.getShop().getName());
            }
        }

        return response;
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在: " + username));
    }

    private Shop getShop(Long shopId) {
        return shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("店铺不存在: " + shopId));
    }

    private ShopReview getShopReview(Long shopReviewId) {
        return shopReviewRepository.findById(shopReviewId)
                .orElseThrow(() -> new ResourceNotFoundException("评价不存在: " + shopReviewId));
    }

    private void validateShopExists(Long shopId) {
        // 不再抛出异常，而是允许自动创建缺失的Shop
        // 由getShop方法处理缺失情况
    }
}

