package com.whocares.musicalapi.service.impl;

import com.whocares.musicalapi.entity.Shop;
import com.whocares.musicalapi.entity.Theater;
import com.whocares.musicalapi.repository.MusicalRepository;
import com.whocares.musicalapi.repository.ReviewRepository;
import com.whocares.musicalapi.repository.TheaterRepository;
import com.whocares.musicalapi.repository.UserRepository;
import com.whocares.musicalapi.service.TheaterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class TheaterServiceImpl implements TheaterService {

    @Autowired
    private TheaterRepository theaterRepository;

    @Override
    public List<Theater> getAllTheaters() {
        return theaterRepository.findAll();
    }



}
