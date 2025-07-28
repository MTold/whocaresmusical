package com.whocares.musicalapi.dto.response;


import com.whocares.musicalapi.entity.Shop;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class ShopResponse {
    private Long id;
    private String name;
    private String location;
    private Integer category;
    private String image;
}
