package com.whocares.musicalapi.service;

import com.whocares.musicalapi.entity.Musical;

import java.util.List;

public interface MusicalService {

    // 获取所有音乐剧
    List<Musical> findAllMusicals();

    // 获取单个音乐剧和演出排期
    Musical getMusicalWithShows(Long id);

    // 添加或更新音乐剧
    Long saveOrUpdateMusical(Musical musical);

    // 查询指定 id 的剧目
    Musical getMusicalById(Long id);



    // 删除音乐剧
    void deleteMusical(Long id);


    // 获取所有原创的音乐剧
    List<Musical> getOriginalMusicals();

    // 获取所有非原创的音乐剧
    List<Musical> getNonOriginalMusicals();

    // 获取按评分排序的前10个音乐剧（null转为0，排除0分剧目）
    List<Musical> getTopRatedMusicals(int limit);
}

