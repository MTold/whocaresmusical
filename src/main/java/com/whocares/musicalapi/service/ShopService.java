package com.whocares.musicalapi.service;

import com.whocares.musicalapi.entity.Review;
import com.whocares.musicalapi.entity.Shop;
import com.whocares.musicalapi.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface ShopService {

    String syncAllTheaterShops();

    String syncSingleTheaterShops(Long theaterId);


}