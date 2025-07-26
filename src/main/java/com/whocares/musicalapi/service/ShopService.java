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



    //根据剧院id查找店铺
    Set<Shop> getShopsByTheaterId(Integer theaterId);

    /**public List<Shop> getShopsByTheaterLocation(String locationName) {
        return ShopRepository.findByTheaterLoc(locationName);
    }*/

    /**public List<Review> getReviewsByShopName(String shopName) {
        return ShopReviewRepository.findByShopName(shopName);
    }*/

    /**public void addReview(Review review) {
        ShopReviewRepository.save(review);
    }*/
}