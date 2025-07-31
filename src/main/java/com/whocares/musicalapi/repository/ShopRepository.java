package com.whocares.musicalapi.repository;

import com.whocares.musicalapi.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

    // 根据高德ID查询店铺，判断是否已存在
    Optional<Shop> findByGaodeId(String gaodeId);

}
