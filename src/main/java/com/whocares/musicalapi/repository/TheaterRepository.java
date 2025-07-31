package com.whocares.musicalapi.repository;

import com.whocares.musicalapi.entity.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TheaterRepository  extends JpaRepository<Theater, Long> {

    //剧院名称模糊查询
    List<Theater> findByNameContainingIgnoreCase(String name) ;

    //待补充功能：离我最近的剧院

    // 按名称或位置的关键词搜索剧院
    List<Theater> findByNameContainingOrLocationNameContaining(String nameKeyword, String locationKeyword);

    // 通过ID列表查询多个剧院
    @Query("SELECT t FROM Theater t WHERE t.id IN :ids")
    List<Theater> findByTheaterIds(@Param("ids") List<Long> ids);
}


