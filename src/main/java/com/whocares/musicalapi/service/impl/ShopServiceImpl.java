package com.whocares.musicalapi.service.impl;

import com.whocares.musicalapi.entity.Shop;
import com.whocares.musicalapi.entity.Theater;
import com.whocares.musicalapi.repository.ShopRepository;
import com.whocares.musicalapi.repository.TheaterRepository;
import com.whocares.musicalapi.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ShopServiceImpl implements ShopService {

    @Autowired
    private final ShopRepository shopRepository;

    @Autowired
    private final TheaterRepository theaterRepository;

    public ShopServiceImpl(ShopRepository shopRepository, TheaterRepository theaterRepository) {
        this.shopRepository = shopRepository;
        this.theaterRepository = theaterRepository;
    }


}
