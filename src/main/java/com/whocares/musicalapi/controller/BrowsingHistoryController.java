package com.whocares.musicalapi.controller;

import com.whocares.musicalapi.dto.response.BrowsingHistoryResponse;
import com.whocares.musicalapi.service.BrowsingHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/history")
public class BrowsingHistoryController {

    private final BrowsingHistoryService browsingHistoryService;

    @Autowired
    public BrowsingHistoryController(BrowsingHistoryService browsingHistoryService) {
        this.browsingHistoryService = browsingHistoryService;
    }

    /**
     * 记录用户浏览剧目
     */
    @PostMapping("/browse/{musicalId}")
    public ResponseEntity<Void> recordBrowsing(
            @PathVariable Long musicalId,
            Authentication authentication) {
        
        String username = authentication.getName();
        browsingHistoryService.recordBrowsing(username, musicalId);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取用户的浏览历史
     */
    @GetMapping
    public ResponseEntity<Page<BrowsingHistoryResponse>> getBrowsingHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        String username = authentication.getName();
        Page<BrowsingHistoryResponse> historyPage = browsingHistoryService.getBrowsingHistory(username, page, size);
        return ResponseEntity.ok(historyPage);
    }

    /**
     * 获取浏览历史数量
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getBrowsingHistoryCount(Authentication authentication) {
        String username = authentication.getName();
        long count = browsingHistoryService.getBrowsingHistoryCount(username);
        return ResponseEntity.ok(count);
    }

    /**
     * 清空浏览历史
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearBrowsingHistory(Authentication authentication) {
        String username = authentication.getName();
        browsingHistoryService.clearBrowsingHistory(username);
        return ResponseEntity.ok().build();
    }

    /**
     * 删除单条浏览记录
     */
    @DeleteMapping("/{historyId}")
    public ResponseEntity<Void> deleteBrowsingHistory(
            @PathVariable Long historyId,
            Authentication authentication) {
        
        String username = authentication.getName();
        browsingHistoryService.deleteBrowsingHistory(username, historyId);
        return ResponseEntity.ok().build();
    }
}