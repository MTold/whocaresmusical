package com.whocares.musicalapi.service.impl;

import com.whocares.musicalapi.entity.Show;
import com.whocares.musicalapi.repository.ShowRepository;
import com.whocares.musicalapi.service.ShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ShowServiceImpl implements ShowService {

    private final ShowRepository showRepository;

    @Autowired
    public ShowServiceImpl(ShowRepository showRepository) {
        this.showRepository = showRepository;
    }

    // 查询所有演出排期
    @Override
    public List<Show> findAllShows() {
        List<Show> shows = showRepository.findAllShows();
        return showRepository.findAllShows();
    }

    // 根据音乐剧ID获取所有演出排期
    @Override
    public List<Show> getShowsByMusicalId(Long musicalId) {
        return showRepository.findByMusicalId(musicalId);
    }

    // 根据剧院ID获取所有演出排期
    @Override
    public List<Show> getShowsByTheaterId(Long theaterId) {
        return showRepository.findByTheaterId(theaterId);
    }

    // 保存或更新演出排期
    @Override
    public Long saveOrUpdateShow(Show show) {
        showRepository.save(show);
        return show.getId();
    }

    // 删除演出排期
    @Override
    public void deleteShow(Long id) {
        showRepository.deleteById(id);
    }
}
