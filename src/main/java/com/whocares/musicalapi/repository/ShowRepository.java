package com.whocares.musicalapi.repository;

import com.whocares.musicalapi.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {

    // 获取指定 musical_id 的所有演出
    List<Show> findByMusicalId(Long musicalId);

    // 获取指定 theater_id 的所有演出
    List<Show> findByTheaterId(Long theaterId);

    @Query("SELECT s FROM Show s")
    List<Show> findAllShows();
}
