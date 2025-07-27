package com.whocares.musicalapi.repository;

import com.whocares.musicalapi.dto.response.ShowResponse;
import com.whocares.musicalapi.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {

    // 获取指定 musical_id 的所有演出 (带 musicalName)
    @Query("SELECT new com.whocares.musicalapi.dto.response.ShowResponse(" +
            "s.id, s.theaterId, s.musicalId, m.name, s.date, s.time, s.cast) " +
            "FROM Show s JOIN Musical m ON s.musicalId = m.id " +
            "WHERE s.musicalId = :musicalId")
    List<ShowResponse> findByMusicalId(Long musicalId);

    // 获取指定 theater_id 的所有演出 (带 musicalName)
    @Query("SELECT new com.whocares.musicalapi.dto.response.ShowResponse(" +
            "s.id, s.theaterId, s.musicalId, m.name, s.date, s.time, s.cast) " +
            "FROM Show s JOIN Musical m ON s.musicalId = m.id " +
            "WHERE s.theaterId = :theaterId")
    List<ShowResponse> findByTheaterId(Long theaterId);

    // 查询指定年份 + 指定月份的演出 (带 musicalName)
        @Query("SELECT new com.whocares.musicalapi.dto.response.ShowResponse(" +
                "s.id, s.theaterId, s.musicalId, m.name, s.date, s.time, s.cast) " +
                "FROM Show s JOIN Musical m ON s.musicalId = m.id " +
                "WHERE YEAR(s.date) = :year AND MONTH(s.date) = :month")
        List<ShowResponse> findByYearAndMonth(int year, int month);


    // 返回指定日期的演出 (带 musicalName)
    @Query("SELECT new com.whocares.musicalapi.dto.response.ShowResponse(" +
            "s.id, s.theaterId, s.musicalId, m.name, s.date, s.time, s.cast) " +
            "FROM Show s JOIN Musical m ON s.musicalId = m.id " +
            "WHERE s.date = :date")
    List<ShowResponse> findByDate(LocalDate date);

    // 返回所有演出 (带 musicalName)
    @Query("SELECT new com.whocares.musicalapi.dto.response.ShowResponse(" +
            "s.id, s.theaterId, s.musicalId, m.name, s.date, s.time, s.cast) " +
            "FROM Show s JOIN Musical m ON s.musicalId = m.id")
    List<ShowResponse> findAllShows();
}
