package com.whocares.musicalapi.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ShowResponse {
    private Long id;  // 演出ID
    private Long musicalId;  // 音乐剧ID
    private LocalDate date;  // 演出日期
    private LocalTime time;  // 演出日期
    private String cast;  // 演员名单
}

