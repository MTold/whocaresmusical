package com.whocares.musicalapi.service;

import com.whocares.musicalapi.entity.Show;

import java.util.List;

public interface ShowService {
    // 查询所有演出排期
    List<Show> findAllShows();

    // 根据音乐剧ID获取所有演出排期
    List<Show> getShowsByMusicalId(Long musicalId);

    // 根据剧院ID获取所有演出排期
    List<Show> getShowsByTheatreId(Long theatreId);

    // 保存或更新演出排期
    Long saveOrUpdateShow(Show show);

    // 删除演出排期
    void deleteShow(Long id);
}
