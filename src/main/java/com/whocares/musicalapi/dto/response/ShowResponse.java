package com.whocares.musicalapi.dto.response;

import com.whocares.musicalapi.entity.Show;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class ShowResponse {
    private Long id;  // 演出ID
    private Long musicalId;  // 音乐剧ID
    private LocalDate date;  // 演出日期
    private LocalTime time;  // 演出日期
    private String cast;  // 演员名单
    private List<Show> shows;
}

