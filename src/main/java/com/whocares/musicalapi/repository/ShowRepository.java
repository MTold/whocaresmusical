package com.whocares.musicalapi.repository;

import com.whocares.musicalapi.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShowRepository extends JpaRepository<Show, Long> {
    // 根据音乐剧ID查询所有演出排期
    List<Show> findByMusicalId(Long musicalId);

    // 根据剧院ID查询所有演出排期
    List<Show> findByTheatreId(Long theatreId);
}
