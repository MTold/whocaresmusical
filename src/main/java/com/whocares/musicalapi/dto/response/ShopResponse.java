package com.whocares.musicalapi.dto.response;

import com.whocares.musicalapi.entity.Shop;
import com.whocares.musicalapi.entity.Theater;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopResponse {
    private Long id;
    private String name;
    private String location;
    private Integer category;

}
