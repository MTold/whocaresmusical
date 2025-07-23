package com.whocares.musicalapi.dto.response;

import lombok.Data;

@Data
public class MusicalSummaryResponse {
    private Long id;
    private String name;
    private String imageUrl;
}
