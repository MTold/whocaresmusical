package com.whocares.musicalapi.dto.response;

import com.whocares.musicalapi.entity.Show;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class ShowResponse {
    private Long id;
    private Long theaterId;
    private Long musicalId;
    private String musicalName; // 新增这个字段
    private LocalDate date;
    private LocalTime time;
    private String cast;

    // 构造函数用于 JPQL 查询
    public ShowResponse(Long id, Long theaterId, Long musicalId, String musicalName,
                        LocalDate date, LocalTime time, String cast) {
        this.id = id;
        this.theaterId = theaterId;
        this.musicalId = musicalId;
        this.musicalName = musicalName;
        this.date = date;
        this.time = time;
        this.cast = cast;
    }
}

