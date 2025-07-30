package com.whocares.musicalapi.service.impl;

import com.whocares.musicalapi.dto.response.MusicalSummaryResponse;
import com.whocares.musicalapi.entity.Musical;
import com.whocares.musicalapi.entity.Show;  // 注释掉与 Show 相关的代码
import com.whocares.musicalapi.repository.MusicalRepository;
import com.whocares.musicalapi.repository.ReviewRepository;
import com.whocares.musicalapi.repository.ShowRepository;  // 注释掉与 Show 相关的代码
import com.whocares.musicalapi.service.MusicalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MusicalServiceImpl implements MusicalService {

    private final MusicalRepository musicalRepository;
    private final ShowRepository showRepository;  // Show相关的代码
    private final ReviewRepository reviewRepository;

    @Autowired
    public MusicalServiceImpl(MusicalRepository musicalRepository, ShowRepository showRepository, ReviewRepository reviewRepository) {
        this.musicalRepository = musicalRepository;
        this.showRepository = showRepository;
        this.reviewRepository = reviewRepository;
    }

    // 查询所有音乐剧
    @Override
    public List<Musical> findAllMusicals() {
        return musicalRepository.findAll();
    }

    // 获取单个音乐剧（注释掉与演出排期相关的代码）
    @Override
    public Musical getMusicalWithShows(Long id) {
        Optional<Musical> musical = musicalRepository.findById(id);
        if (musical.isPresent()) {
            // 注释掉与演出排期相关的代码
            // List<Show> shows = showRepository.findByMusicalId(id);  // 查询与该音乐剧相关的演出排期
            // musical.get().setShows(shows);  // 将演出排期信息设置到音乐剧对象中
            return musical.get();  // 返回音乐剧对象
        }
        return null;
    }

    @Override
    public Musical getMusicalById(Long id) {
        return musicalRepository.findById(id).orElse(null);
    }

    // 保存或更新音乐剧
    @Override
    public Long saveOrUpdateMusical(Musical musical) {
        musicalRepository.save(musical);
        return musical.getId();
    }

    // 删除音乐剧
    @Override
    public void deleteMusical(Long id) {
        musicalRepository.deleteById(id);
    }

    @Override
    public List<Musical> getOriginalMusicals() {
        return musicalRepository.findByIsOriginalTrue();
    }

    @Override
    public List<Musical> getNonOriginalMusicals() {
        return musicalRepository.findByIsOriginalFalse();
    }

    // 获取按评分排序的前N个音乐剧（基于reviews表计算）
    @Override
    public List<Musical> getTopRatedMusicals(int limit) {
        try {
            // 获取所有有评分的剧目ID及其平均评分
            List<Object[]> ratingsData = reviewRepository.getMusicalIdsWithAverageRatings();
            
            if (ratingsData.isEmpty()) {
                return new ArrayList<>();
            }
            
            // 根据评分排序并获取对应的剧目
            return ratingsData.stream()
                    .limit(limit)
                    .map(data -> {
                        if (data == null || data.length < 2 || data[0] == null) {
                            return null;
                        }
                        Long musicalId = ((Number) data[0]).longValue();
                        Double avgRating = (Double) data[1];
                        
                        if (musicalId == null) {
                            return null;
                        }
                        
                        Musical musical = musicalRepository.findById(musicalId).orElse(null);
                        if (musical != null) {
                            // 设置计算出的平均评分
                            musical.setAverageRating(avgRating);
                        }
                        return musical;
                    })
                    .filter(musical -> musical != null)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

}
