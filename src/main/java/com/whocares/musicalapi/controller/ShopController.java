package com.whocares.musicalapi.controller;

import com.whocares.musicalapi.dto.response.ShopResponse;
import com.whocares.musicalapi.entity.Review;
import com.whocares.musicalapi.entity.Shop;
import com.whocares.musicalapi.security.jwt.JwtTokenProvider;
import com.whocares.musicalapi.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/shops")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ShopController {
    @Autowired
    private ShopService shopService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    //获取剧院对应的所有店铺
    @GetMapping("/theaters/{theaterId}/shops")
    public ResponseEntity<Set<ShopResponse>> getShopsByTheaterId(@PathVariable Integer theaterId) {
        Set<Shop> shops = shopService.getShopsByTheaterId(theaterId);
        Set<ShopResponse> responses = shops.stream()
                .map(shop -> new ShopResponse(shop.getId(), shop.getName(), shop.getLocation(), shop.getCategory()))
                .collect(Collectors.toSet());
        return ResponseEntity.ok(responses);
    }
}