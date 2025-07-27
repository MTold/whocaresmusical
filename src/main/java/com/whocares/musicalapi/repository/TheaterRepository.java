package com.whocares.musicalapi.repository;

import com.whocares.musicalapi.entity.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TheaterRepository  extends JpaRepository<Theater, Long> {

    //剧院名称模糊查询
    List<Theater> findByNameContainingIgnoreCase(String name) ;

    //待补充功能：离我最近的剧院
}


