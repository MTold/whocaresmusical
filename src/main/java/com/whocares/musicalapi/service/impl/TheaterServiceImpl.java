package com.whocares.musicalapi.service.impl;
import com.whocares.musicalapi.entity.Shop;
import com.whocares.musicalapi.entity.Theater;
import com.whocares.musicalapi.repository.TheaterRepository;
import com.whocares.musicalapi.service.TheaterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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
    public Long saveOrUpdateTheater(Theater theater) {
        if (theater.getId() != null) {
            Theater existingTheater = theaterRepository.findById(theater.getId()).orElse(null);
            if (existingTheater != null) {
                existingTheater.setName(theater.getName());
                existingTheater.setLocationName(theater.getLocationName());
                existingTheater.setLatitude(theater.getLatitude());
                existingTheater.setLongitude(theater.getLongitude());
                existingTheater.setImageUrl(theater.getImageUrl());
                return theaterRepository.save(existingTheater).getId();
            }
        }
        return theaterRepository.save(theater).getId();
    }

    @Override
    //查询指定id的剧院
    public Theater getTheaterById(Long theaterId){
        return theaterRepository.findById(theaterId).orElse(null);
    };

    @Override
    //删除剧院
    public void deleteTheaterById(Long Id){theaterRepository.deleteById(Id);}

    //根据剧院id查找店铺
    @Override
    public List<Shop> findShopsByTheaterId(Long theaterId) {
        Theater theater = theaterRepository.findById(theaterId).orElse(null);
        return theater.getShops();
    }

    @Override
    public List<Shop> findShopsByTheaterIdAndCategory(Long theaterId, Integer category) {
        Theater theater = theaterRepository.findById(theaterId).orElse(null);
        List<Shop> shops = theater.getShops();
        return shops.stream()  // 创建流
                .filter(shop -> Objects.equals(shop.getCategory(), category))  // 筛选category等于指定值的Shop
                .collect(Collectors.toList());
    }

    @Override
    public Theater findTheaterById(Long id) {
        return theaterRepository.findById(id).orElse(null);
    }

}
