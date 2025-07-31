package com.whocares.musicalapi.repository;

import com.whocares.musicalapi.entity.TheaterShop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TheaterShopRepository extends JpaRepository<TheaterShop, Long> {

    // 检查关联是否已存在
    boolean existsByTheaterIdAndShopId(Long theaterId, Long shopId);


    // 按店铺ID查询所有关联
    List<TheaterShop> findByShopId(Long shopId);

    // 按店铺ID删除所有关联
    void deleteByShopId(Long shopId);
}
