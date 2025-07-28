package com.whocares.musicalapi.service.impl;
import com.whocares.musicalapi.entity.Theater;
import com.whocares.musicalapi.repository.TheaterRepository;
import com.whocares.musicalapi.service.TheaterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TheaterServiceImpl implements TheaterService {

    private final TheaterRepository theaterRepository;

    public TheaterServiceImpl(TheaterRepository theaterRepository) {
        this.theaterRepository = theaterRepository;
    }

    //获得所有剧院
    @Override
    public List<Theater> findAllTheaters(){return theaterRepository.findAll();} //这里不能写成TheaterRepository

    // 添加或更新剧院
    @Override
    public Long saveOrUpdateTheater(Theater theater){
        theaterRepository.save(theater);
        return theater.getId();
    }

    @Override
    //查询指定id的剧院
    public Theater getTheaterById(Long theaterId){
        return theaterRepository.findById(theaterId).orElse(null);
    };

    @Override
    //删除剧院
    public void deleteTheaterById(Long Id){theaterRepository.deleteById(Id);}
}
