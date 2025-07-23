package com.whocares.musicalapi.service.impl;

import com.whocares.musicalapi.dto.response.BrowsingHistoryResponse;
import com.whocares.musicalapi.entity.Musical;
import com.whocares.musicalapi.entity.User;
import com.whocares.musicalapi.entity.UserBrowsingHistory;
import com.whocares.musicalapi.exception.ResourceNotFoundException;
import com.whocares.musicalapi.repository.MusicalRepository;
import com.whocares.musicalapi.repository.UserBrowsingHistoryRepository;
import com.whocares.musicalapi.repository.UserRepository;
import com.whocares.musicalapi.service.BrowsingHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BrowsingHistoryServiceImpl implements BrowsingHistoryService {

    private final UserBrowsingHistoryRepository browsingHistoryRepository;
    private final UserRepository userRepository;
    private final MusicalRepository musicalRepository;

    @Autowired
    public BrowsingHistoryServiceImpl(UserBrowsingHistoryRepository browsingHistoryRepository,
                                    UserRepository userRepository,
                                    MusicalRepository musicalRepository) {
        this.browsingHistoryRepository = browsingHistoryRepository;
        this.userRepository = userRepository;
        this.musicalRepository = musicalRepository;
    }

    @Override
    public void recordBrowsing(String username, Long musicalId) {
        User user = getUserByUsername(username);
        Musical musical = getMusicalById(musicalId);

        // 检查是否已存在浏览记录
        UserBrowsingHistory existingHistory = 
            browsingHistoryRepository.findByUserUserIdAndMusicalId(user.getUserId(), musicalId)
                .orElse(null);

        if (existingHistory != null) {
            // 更新浏览时间
            existingHistory.setViewedAt(java.time.LocalDateTime.now());
            browsingHistoryRepository.save(existingHistory);
        } else {
            // 创建新的浏览记录
            UserBrowsingHistory history = new UserBrowsingHistory(user, musical);
            browsingHistoryRepository.save(history);
            
            // 检查并清理超出10条的记录
            cleanOldBrowsingHistory(user.getUserId());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BrowsingHistoryResponse> getBrowsingHistory(String username, int page, int size) {
        User user = getUserByUsername(username);
        Pageable pageable = PageRequest.of(page, size, Sort.by("viewedAt").descending());
        
        Page<UserBrowsingHistory> historyPage = 
            browsingHistoryRepository.findByUserUserIdOrderByViewedAtDesc(user.getUserId(), pageable);
        
        return historyPage.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public long getBrowsingHistoryCount(String username) {
        User user = getUserByUsername(username);
        return browsingHistoryRepository.countByUserUserId(user.getUserId());
    }

    @Override
    public void clearBrowsingHistory(String username) {
        User user = getUserByUsername(username);
        browsingHistoryRepository.deleteByUserUserId(user.getUserId());
    }

    @Override
    public void deleteBrowsingHistory(String username, Long historyId) {
        User user = getUserByUsername(username);
        
        UserBrowsingHistory history = browsingHistoryRepository.findById(historyId)
            .orElseThrow(() -> new ResourceNotFoundException("浏览记录不存在: " + historyId));
        
        // 确保用户只能删除自己的浏览记录
        if (!history.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalStateException("无法删除其他用户的浏览记录");
        }
        
        browsingHistoryRepository.delete(history);
    }

    private void cleanOldBrowsingHistory(Long userId) {
        List<UserBrowsingHistory> histories = 
            browsingHistoryRepository.findByUserUserIdOrderByViewedAtDesc(userId, Pageable.unpaged()).getContent();
        
        if (histories.size() > 10) {
            List<Long> idsToDelete = histories.stream()
                .skip(10)
                .map(UserBrowsingHistory::getId)
                .collect(Collectors.toList());
            
            if (!idsToDelete.isEmpty()) {
                browsingHistoryRepository.deleteAllById(idsToDelete);
            }
        }
    }

    private BrowsingHistoryResponse convertToResponse(UserBrowsingHistory history) {
        BrowsingHistoryResponse response = new BrowsingHistoryResponse();
        response.setId(history.getId());
        response.setMusicalId(history.getMusical().getId());
        response.setMusicalName(history.getMusical().getName());
        response.setMusicalImage(history.getMusical().getImageUrl());
        response.setMusicalDescription(history.getMusical().getDescription());
        response.setMusicalVenue(history.getMusical().getVenue());
        response.setViewedAt(history.getViewedAt());
        
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