package com.whocares.musicalapi.repository;

import com.whocares.musicalapi.entity.Shop;
import io.micrometer.common.lang.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

    // 根据高德ID查询店铺，判断是否已存在
    Optional<Shop> findByGaodeId(String gaodeId);


    Optional<Shop> findByName(String name);

    //根据店铺名关键词检索
    List<Shop> findByNameContaining(String keyword);

    //新加的
    Optional<Shop> findByNameAndAddress(String name, String address);

    // 新增：按店铺名称关键词分页查询
    Page<Shop> findByNameContaining(String keyword, Pageable pageable);

    // 新增：按ID集合分页查询（用于剧院关键词筛选后分页）
    Page<Shop> findByIdIn(Collection<Long> ids, Pageable pageable);

    // 新增：统计ID在集合中的总条数（用于计算筛选后的总页数）
    long countByIdIn(Collection<Long> ids);


}
