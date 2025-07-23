package com.whocares.musicalapi.service;

import com.whocares.musicalapi.dto.response.BrowsingHistoryResponse;
import org.springframework.data.domain.Page;

public interface BrowsingHistoryService {
    
    /**
     * 记录用户浏览剧目
     * @param username 用户名
     * @param musicalId 剧目ID
     */
    void recordBrowsing(String username, Long musicalId);
    
    /**
     * 获取用户的浏览历史
     * @param username 用户名
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 浏览历史分页数据
     */
    Page<BrowsingHistoryResponse> getBrowsingHistory(String username, int page, int size);
    
    /**
     * 获取用户的浏览历史数量
     * @param username 用户名
     * @return 浏览历史总数
     */
    long getBrowsingHistoryCount(String username);
    
    /**
     * 清空用户的浏览历史
     * @param username 用户名
     */
    void clearBrowsingHistory(String username);
    
    /**
     * 删除单条浏览记录
     * @param username 用户名
     * @param historyId 浏览记录ID
     */
    void deleteBrowsingHistory(String username, Long historyId);
}