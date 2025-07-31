package com.whocares.musicalapi.controller;

import com.whocares.musicalapi.dto.response.ShopResponse;
import com.whocares.musicalapi.dto.response.ShopWithTheatersDTO;
import com.whocares.musicalapi.entity.Theater;
import com.whocares.musicalapi.service.ShopService;
import com.whocares.musicalapi.entity.Shop;
import com.whocares.musicalapi.security.jwt.JwtTokenProvider;
import com.whocares.musicalapi.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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





    /**
     * 搜索店铺
     * @param shopName 店铺名称关键词
     * @param theaterKeyword 剧院关键词
     * @param page 页码（从0开始，前端通常传1，需转换）
     * @param size 每页条数
     */
    @GetMapping("/search")
    public ResponseEntity<Page<ShopWithTheatersDTO>> searchShops(
            @RequestParam(required = false) String shopName,
            @RequestParam(required = false) String theaterKeyword,
            @RequestParam(defaultValue = "0") int page,  // 页码（默认第0页）
            @RequestParam(defaultValue = "10") int size) {
        Page<ShopWithTheatersDTO> result = shopService.searchShops(shopName, theaterKeyword, page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取店铺及其关联的剧院
     */
    @GetMapping("/{id}/with-theaters")
    public ResponseEntity<ShopWithTheatersDTO> getShopWithTheaters(@PathVariable Long id) {
        ShopWithTheatersDTO shop = shopService.getShopWithTheaters(id);
        return ResponseEntity.ok(shop);
    }

    /**
     * 添加店铺
     */
    @PostMapping
    public ResponseEntity<Shop> addShop(@RequestBody ShopWithTheatersDTO shopWithTheaters) {
        Shop savedShop = shopService.addShop(shopWithTheaters);
        return ResponseEntity.ok(savedShop);
    }

    /**
     * 更新店铺
     */
    @PutMapping("/{id}")
    public ResponseEntity<Shop> updateShop(
            @PathVariable Long id,
            @RequestBody ShopWithTheatersDTO shopWithTheaters) {
        Shop updatedShop = shopService.updateShop(id, shopWithTheaters);
        return ResponseEntity.ok(updatedShop);
    }

    /**
     * 删除店铺
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShop(@PathVariable Long id) {
        shopService.deleteShop(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 获取所有剧院（用于下拉选择）
     */
    @GetMapping("/theaters")
    public ResponseEntity<List<Theater>> getAllTheaters() {
        List<Theater> theaters = shopService.getAllTheaters();
        return ResponseEntity.ok(theaters);
    }

}