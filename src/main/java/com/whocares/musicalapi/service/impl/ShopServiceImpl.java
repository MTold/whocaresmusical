package com.whocares.musicalapi.service.impl;

import com.whocares.musicalapi.entity.Shop;
import com.whocares.musicalapi.repository.ShopRepository;
import com.whocares.musicalapi.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    private ShopRepository shopRepository;

}
