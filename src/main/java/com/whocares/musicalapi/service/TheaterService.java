package com.whocares.musicalapi.service;

import com.whocares.musicalapi.entity.Shop;
import com.whocares.musicalapi.entity.Theater;

import java.util.List;
import java.util.Set;

public interface TheaterService {

    // 获取所有剧院
    List<Theater> findAllTheaters();

    // 添加或更新剧院
    Long saveOrUpdateTheater(Theater theater);

    //查询指定id的剧院
    Theater getTheaterById(Long theaterId);

    //删除剧院
    void deleteTheaterById(Long theaterId);

    List<Shop> findShopsByTheaterId(Long theaterId);

    List<Shop> findShopsByTheaterIdAndCategory(Long theaterId , Integer category);

    Theater findTheaterById(Long id);
}
