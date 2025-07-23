package com.whocares.musicalapi.repository;

import com.whocares.musicalapi.entity.UserBrowsingHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserBrowsingHistoryRepository extends JpaRepository<UserBrowsingHistory, Long> {
    
    /**
     * 根据用户ID和剧目ID查找浏览记录
     */
    Optional<UserBrowsingHistory> findByUserUserIdAndMusicalId(Long userId, Long musicalId);
    
    /**
     * 获取用户的浏览历史，按时间倒序排列
     */
    Page<UserBrowsingHistory> findByUserUserIdOrderByViewedAtDesc(Long userId, Pageable pageable);
    
    /**
     * 统计用户的浏览历史数量
     */
    long countByUserUserId(Long userId);
    
    /**
     * 删除指定用户的浏览记录
     */
    @Modifying
    @Query("DELETE FROM UserBrowsingHistory ubh WHERE ubh.user.userId = :userId AND ubh.musical.id = :musicalId")
    void deleteByUserIdAndMusicalId(@Param("userId") Long userId, @Param("musicalId") Long musicalId);
    
    /**
     * 检查用户是否浏览过某个剧目
     */
    boolean existsByUserUserIdAndMusicalId(Long userId, Long musicalId);
    
    /**
     * 删除用户的所有浏览历史
     */
    @Modifying
    void deleteByUserUserId(Long userId);
}