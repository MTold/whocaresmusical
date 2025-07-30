package com.whocares.musicalapi.repository;

import com.whocares.musicalapi.entity.Musical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MusicalRepository extends JpaRepository<Musical, Long> {

    // 根据名称模糊查询所有音乐剧
    List<Musical> findByNameContainingIgnoreCase(String name);  // 模糊查询音乐剧名称

    // 查询所有原创的音乐剧（不分页）
    List<Musical> findByIsOriginalTrue();  // 查询所有原创音乐剧

    // 查询所有非原创的音乐剧（不分页）
    List<Musical> findByIsOriginalFalse();  // 查询所有非原创音乐剧

    // 查询指定 id 的剧目
    Musical findById(long id);

    // 查询所有音乐剧并按平均评分降序排列
    @Query("SELECT m FROM Musical m ORDER BY m.averageRating DESC")
    Optional<List<Musical>> findAllByOrderByAverageRatingDesc();

    //    // 获取音乐剧的平均评分和数量
//    @Query("SELECT COUNT(r), AVG(r.rating) FROM Review r WHERE r.musical.id = :musicalId")
//    List<Object[]> getReviewStatistics(@Param("musicalId") Long musicalId);
}
