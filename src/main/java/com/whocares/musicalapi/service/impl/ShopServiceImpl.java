package com.whocares.musicalapi.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.whocares.musicalapi.entity.GaodeMapApiUtil;
import com.whocares.musicalapi.entity.Shop;
import com.whocares.musicalapi.entity.Theater;
import com.whocares.musicalapi.entity.TheaterShop;
import com.whocares.musicalapi.repository.ShopRepository;
import com.whocares.musicalapi.repository.TheaterRepository;
import com.whocares.musicalapi.repository.TheaterShopRepository;
import com.whocares.musicalapi.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ShopServiceImpl implements ShopService {

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private TheaterShopRepository theaterShopRepository;

    @Autowired
    private GaodeMapApiUtil gaodeMapApiUtil;

    // 搜索半径：100米
    //private static final int SEARCH_RADIUS = 100;

    // 店铺类型映射
    private static final String[][] SHOP_TYPES = {
            {"050000", "1"},  // 餐饮
            {"100000", "2"},  // 住宿
            {"110000", "3"}   // 景点
    };

    /**
     * 同步所有剧院的周边店铺数据
     * @return 同步结果
     */
    @Transactional
    @Override
    public String syncAllTheaterShops() {
        // 1. 获取所有剧院
        List<Theater> theaters = theaterRepository.findAll();
        if (theaters.isEmpty()) {
            return "没有找到任何剧院数据";
        }

        int totalShopsAdded = 0;
        int totalRelationsAdded = 0;

        // 2. 遍历每个剧院
        for (Theater theater : theaters) {
            // 3. 搜索三种类型的店铺
            for (String[] type : SHOP_TYPES) {
                String typeCode = type[0];
                int category = Integer.parseInt(type[1]);
                //我自己加的
                int SEARCH_RADIUS = 500;
                /*if (category == 1 ) { SEARCH_RADIUS = 500;}
                else if (category == 2 ) { SEARCH_RADIUS = 3000;}
                else if (category == 3 ) { SEARCH_RADIUS = 2000;}*/
                // 调用高德API获取周边店铺
                JSONArray shops = gaodeMapApiUtil.searchAroundShops(
                        theater.getLongitude(),
                        theater.getLatitude(),
                        SEARCH_RADIUS,
                        typeCode
                );

                // 处理返回的店铺数据
                for (int i = 0; i < shops.size(); i++) {
                    JSONObject shopJson = shops.getJSONObject(i);
                    String gaodeId = shopJson.getString("id");

                    // 检查店铺是否已存在
                    Shop shop = shopRepository.findByGaodeId(gaodeId).orElse(null);

                    // 如果店铺不存在，则创建新店铺
                    if (shop == null) {
                        shop = new Shop();
                        shop.setGaodeId(gaodeId);
                        shop.setName(shopJson.getString("name"));
                        shop.setAddress(shopJson.getString("address"));
                        shop.setCategory(category);

                        // 处理图片
                        if (shopJson.containsKey("photos")) {
                            JSONArray photos = shopJson.getJSONArray("photos");
                            if (!photos.isEmpty()) {
                                JSONObject firstPhoto = photos.getJSONObject(0);
                                shop.setImage(firstPhoto.getString("url").trim());
                            }
                        }

                        shop = shopRepository.save(shop);
                        totalShopsAdded++;
                    }

                    // 检查关联是否已存在
                    boolean relationExists = theaterShopRepository.existsByTheaterIdAndShopId(
                            theater.getId(), shop.getId()
                    );

                    // 如果关联不存在，则创建新关联
                    if (!relationExists) {
                        TheaterShop theaterShop = new TheaterShop();
                        theaterShop.setTheaterId(theater.getId());
                        theaterShop.setShopId(shop.getId());
                        theaterShopRepository.save(theaterShop);
                        totalRelationsAdded++;
                    }
                }
            }
        }

        return String.format("同步完成，新增店铺: %d 个，新增关联: %d 个",
                totalShopsAdded, totalRelationsAdded);
    }


    /**
     * 同步单个剧院的周边店铺数据
     * @param theaterId 剧院ID
     * @return 同步结果
     */
    @Transactional
    @Override
    public String syncSingleTheaterShops(Long theaterId) {
        // 获取指定剧院
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new RuntimeException("未找到ID为" + theaterId + "的剧院"));

        int totalShopsAdded = 0;
        int totalRelationsAdded = 0;

        // 搜索三种类型的店铺
        for (String[] type : SHOP_TYPES) {
            String typeCode = type[0];
            int category = Integer.parseInt(type[1]);

            //我自己加的
            int SEARCH_RADIUS = 500;
            /*if (category == 1 ) { SEARCH_RADIUS = 500;}
            else if (category == 2 ) { SEARCH_RADIUS = 3000;}
            else if (category == 3 ) { SEARCH_RADIUS = 2000;}*/

            // 调用高德API获取周边店铺
            JSONArray shops = gaodeMapApiUtil.searchAroundShops(
                    theater.getLongitude(),
                    theater.getLatitude(),
                    SEARCH_RADIUS,
                    typeCode
            );

            // 处理返回的店铺数据
            for (int i = 0; i < shops.size(); i++) {
                JSONObject shopJson = shops.getJSONObject(i);
                String gaodeId = shopJson.getString("id");

                // 检查店铺是否已存在
                Shop shop = shopRepository.findByGaodeId(gaodeId).orElse(null);

                // 如果店铺不存在，则创建新店铺
                if (shop == null) {
                    shop = new Shop();
                    shop.setGaodeId(gaodeId);
                    shop.setName(shopJson.getString("name"));
                    shop.setAddress(shopJson.getString("address"));
                    shop.setCategory(category);

                    // 处理图片
                    if (shopJson.containsKey("photos")) {
                        JSONArray photos = shopJson.getJSONArray("photos");
                        if (!photos.isEmpty()) {
                            JSONObject firstPhoto = photos.getJSONObject(0);
                            shop.setImage(firstPhoto.getString("url").trim());
                        }
                    }

                    shop = shopRepository.save(shop);
                    totalShopsAdded++;
                }

                // 检查关联是否已存在
                boolean relationExists = theaterShopRepository.existsByTheaterIdAndShopId(
                        theater.getId(), shop.getId()
                );

                // 如果关联不存在，则创建新关联
                if (!relationExists) {
                    TheaterShop theaterShop = new TheaterShop();
                    theaterShop.setTheaterId(theater.getId());
                    theaterShop.setShopId(shop.getId());
                    theaterShopRepository.save(theaterShop);
                    totalRelationsAdded++;
                }
            }
        }

        return String.format("剧院ID: %d 同步完成，新增店铺: %d 个，新增关联: %d 个",
                theaterId, totalShopsAdded, totalRelationsAdded);

    }
}
