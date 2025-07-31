package com.whocares.musicalapi.repository;

import com.whocares.musicalapi.entity.TheaterShop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TheaterShopRepository extends JpaRepository<TheaterShop, Long> {

    // 检查关联是否已存在
    boolean existsByTheaterIdAndShopId(Long theaterId, Long shopId);
}
