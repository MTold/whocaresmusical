package com.whocares.musicalapi.controller;

import com.whocares.musicalapi.entity.Musical;
import com.whocares.musicalapi.service.MusicalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/musicals")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MusicalController {

    private final MusicalService musicalService;

    @Autowired
    public MusicalController(MusicalService musicalService) {
        this.musicalService = musicalService;
    }

    //获取所有音乐剧
    @GetMapping
    public ResponseEntity<List<Musical>> getAllMusicals() {
        List<Musical> musicalSummaryResponses = musicalService.findAllMusicals();
        if (musicalSummaryResponses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(musicalSummaryResponses);
    }

    // 获取所有原创的音乐剧
    @GetMapping("/original")
    public ResponseEntity<List<Musical>> getOriginalMusicals() {
        List<Musical> originalMusicals = musicalService.getOriginalMusicals();
        return ResponseEntity.ok(originalMusicals);
    }

    // 获取所有非原创的音乐剧
    @GetMapping("/non-original")
    public ResponseEntity<List<Musical>> getNonOriginalMusicals() {
        List<Musical> nonOriginalMusicals = musicalService.getNonOriginalMusicals();
        return ResponseEntity.ok(nonOriginalMusicals);
    }

//    // 获取单个音乐剧及其演出排期
//    @GetMapping("/{id}")
//    public ResponseEntity<Musical> getMusicalWithShows(@PathVariable Long id) {
//        Musical musical = musicalService.getMusicalWithShows(id);  // 获取音乐剧及其演出排期
//        if (musical == null) {
//            return ResponseEntity.notFound().build();  // 如果没有找到该音乐剧，返回 404
//        }
//        return ResponseEntity.ok(musical);  // 返回找到的音乐剧
//    }

    @GetMapping("/{id}")
    public Musical getMusicalById(@PathVariable Long id) {
        return musicalService.getMusicalById(id);
    }

    // 创建新音乐剧
    @PostMapping
    public ResponseEntity<Long> createMusical(@RequestBody Musical musical) {
        Long createdMusical = musicalService.saveOrUpdateMusical(musical);  // 创建新音乐剧
        return ResponseEntity.ok(createdMusical);  // 返回新创建的音乐剧
    }

    // 更新音乐剧信息
    @PutMapping("/{id}")
    public ResponseEntity<Long> updateMusical(@PathVariable Long id, @RequestBody Musical musical) {
        musical.setId(id);  // 设置ID，以确保更新操作
        Long updatedMusical = musicalService.saveOrUpdateMusical(musical);  // 更新音乐剧
        return ResponseEntity.ok(updatedMusical);  // 返回更新后的音乐剧
    }

    // 删除音乐剧
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMusical(@PathVariable Long id) {
        musicalService.deleteMusical(id);  // 删除音乐剧
        return ResponseEntity.noContent().build();  // 返回无内容响应
    }
    
    // 获取按评分排序的前10个音乐剧
    @GetMapping("/top-rated")
    public ResponseEntity<List<Musical>> getTopRatedMusicals(@RequestParam(defaultValue = "10") int limit) {
        List<Musical> topRatedMusicals = musicalService.getTopRatedMusicals(limit);
        if (topRatedMusicals.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(topRatedMusicals);
    }
}
