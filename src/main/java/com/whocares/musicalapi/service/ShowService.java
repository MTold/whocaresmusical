package com.whocares.musicalapi.service;

import com.whocares.musicalapi.dto.response.ShowResponse;
import com.whocares.musicalapi.entity.Show;

import java.time.LocalDate;
import java.util.List;

public interface ShowService {
    // 查询所有演出排期
    List<ShowResponse> findAllShows();

    // 根据音乐剧ID获取所有演出排期
    List<ShowResponse> getShowsByMusicalId(Long musicalId);

    // 根据剧院ID获取所有演出排期
    List<ShowResponse> getShowsByTheaterId(Long theaterId);

    List<ShowResponse> getShowsByYearAndMonth(int year, int month);

    List<ShowResponse> getShowsByDate(LocalDate date);

    // 保存或更新演出排期
    Long saveOrUpdateShow(Show show);

    // 删除演出排期
    void deleteShow(Long id);
}
