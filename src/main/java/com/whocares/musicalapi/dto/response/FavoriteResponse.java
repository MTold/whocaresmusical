package com.whocares.musicalapi.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteResponse {
    private Long id;
    private Long musicalId;
    private String musicalName;
    private String musicalImage;
    private String musicalDescription;
    private String musicalVenue;
    private LocalDateTime createdAt;
}