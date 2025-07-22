package com.whocares.musicalapi.service.impl;

import com.whocares.musicalapi.dto.response.MusicalSummaryResponse;
import com.whocares.musicalapi.entity.Musical;
import com.whocares.musicalapi.entity.Show;  // 注释掉与 Show 相关的代码
import com.whocares.musicalapi.repository.MusicalRepository;
import com.whocares.musicalapi.repository.ShowRepository;  // 注释掉与 Show 相关的代码
import com.whocares.musicalapi.service.MusicalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MusicalServiceImpl implements MusicalService {

    private final MusicalRepository musicalRepository;
    private final ShowRepository showRepository;  // Show相关的代码

    @Autowired
    public MusicalServiceImpl(MusicalRepository musicalRepository, ShowRepository showRepository) {
        this.musicalRepository = musicalRepository;
        this.showRepository = showRepository;  // Show相关的代码
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

}
