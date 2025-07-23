package com.whocares.musicalapi.service.impl;

import com.whocares.musicalapi.dto.response.FavoriteResponse;
import com.whocares.musicalapi.entity.Musical;
import com.whocares.musicalapi.entity.User;
import com.whocares.musicalapi.entity.UserFavorite;
import com.whocares.musicalapi.exception.ResourceNotFoundException;
import com.whocares.musicalapi.repository.MusicalRepository;
import com.whocares.musicalapi.repository.UserFavoriteRepository;
import com.whocares.musicalapi.repository.UserRepository;
import com.whocares.musicalapi.service.UserFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserFavoriteServiceImpl implements UserFavoriteService {

    private final UserFavoriteRepository userFavoriteRepository;
    private final UserRepository userRepository;
    private final MusicalRepository musicalRepository;

    @Autowired
    public UserFavoriteServiceImpl(UserFavoriteRepository userFavoriteRepository,
                                   UserRepository userRepository,
                                   MusicalRepository musicalRepository) {
        this.userFavoriteRepository = userFavoriteRepository;
        this.userRepository = userRepository;
        this.musicalRepository = musicalRepository;
    }

    @Override
    public void addFavorite(String username, Long musicalId) {
        User user = getUserByUsername(username);
        Musical musical = getMusicalById(musicalId);

        // 检查是否已收藏
        if (userFavoriteRepository.existsByUserIdAndMusicalId(user.getUserId(), musicalId)) {
            throw new IllegalStateException("该剧目已被收藏");
        }

        UserFavorite favorite = new UserFavorite();
        favorite.setUser(user);
        favorite.setMusical(musical);

        userFavoriteRepository.save(favorite);
    }

    @Override
    public void removeFavorite(String username, Long musicalId) {
        User user = getUserByUsername(username);
        
        // 删除收藏记录
        userFavoriteRepository.deleteByUserIdAndMusicalId(user.getUserId(), musicalId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FavoriteResponse> getUserFavorites(String username, int page, int size) {
        User user = getUserByUsername(username);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        Page<UserFavorite> favoritesPage = userFavoriteRepository.findByUserIdOrderByCreatedAtDesc(
            user.getUserId(), pageable);
        
        return favoritesPage.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFavorite(String username, Long musicalId) {
        User user = getUserByUsername(username);
        return userFavoriteRepository.existsByUserIdAndMusicalId(user.getUserId(), musicalId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getFavoriteCount(String username) {
        User user = getUserByUsername(username);
        return userFavoriteRepository.countByUserId(user.getUserId());
    }

    private FavoriteResponse convertToResponse(UserFavorite favorite) {
        FavoriteResponse response = new FavoriteResponse();
        response.setId(favorite.getId());
        response.setCreatedAt(favorite.getCreatedAt());
        response.setMusicalId(favorite.getMusical().getId());
        response.setMusicalName(favorite.getMusical().getName());
        response.setMusicalImage(favorite.getMusical().getImageUrl());
        response.setMusicalDescription(favorite.getMusical().getDescription());
        response.setMusicalVenue(favorite.getMusical().getVenue());
        
        return response;
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在: " + username));
    }

    private Musical getMusicalById(Long musicalId) {
        return musicalRepository.findById(musicalId)
                .orElseThrow(() -> new ResourceNotFoundException("剧目不存在: " + musicalId));
    }
}