package com.whocares.musicalapi.controller;

import com.whocares.musicalapi.dto.response.ShopResponse;
import com.whocares.musicalapi.service.ShopService;
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
    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }


    /**
     * 同步所有剧院的周边店铺
     */
    @PostMapping("/sync/all")
    public ResponseEntity<String> syncAllShops() {
        try {
            String result = shopService.syncAllTheaterShops();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("同步失败: " + e.getMessage());
        }
    }

    /**
     * 同步单个剧院的周边店铺
     */
    @PostMapping("/sync/{theaterId}")
    public ResponseEntity<String> syncSingleTheaterShops(@PathVariable Long theaterId) {
        try {
            String result = shopService.syncSingleTheaterShops(theaterId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("同步失败: " + e.getMessage());
        }
    }

}