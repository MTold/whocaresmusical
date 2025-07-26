package com.whocares.musicalapi.service;

import com.whocares.musicalapi.entity.Shop;
import com.whocares.musicalapi.entity.Theater;
import com.whocares.musicalapi.repository.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface TheaterService {

    //获取所有剧院
    List<Theater> getAllTheaters();


}
