package com.whocares.musicalapi.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class MusicalResponse {
    private Long id;  // 音乐剧ID
    private String name;  // 音乐剧名称
    private String info;  // 音乐剧的描述
    private String is_original;  // 音乐剧是否原创
    private String imageUrl;  // 存储封面图的URL
    private List<ShowResponse> shows;  // 演出排期列表
}
