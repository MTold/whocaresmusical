package com.whocares.musicalapi.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.whocares.musicalapi.dto.response.ShopWithTheatersDTO;
import com.whocares.musicalapi.entity.GaodeMapApiUtil;
import com.whocares.musicalapi.entity.Shop;
import com.whocares.musicalapi.entity.Theater;
import com.whocares.musicalapi.entity.TheaterShop;
import com.whocares.musicalapi.repository.ShopRepository;
import com.whocares.musicalapi.repository.TheaterRepository;
import com.whocares.musicalapi.repository.TheaterShopRepository;
import com.whocares.musicalapi.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    /**
     * 搜索店铺
     * @param shopNameKeyword 店铺名称关键词
     * @param theaterKeyword 剧院名称或位置关键词
     * @param page 页码（从0开始）
     * @param size 每页条数
     * @return 分页后的店铺列表（含总条数、总页数等信息）
     */
    @Transactional
    @Override
    public Page<ShopWithTheatersDTO> searchShops(
            String shopNameKeyword,
            String theaterKeyword,
            int page,
            int size) {
        // 1. 构建分页参数（页码从0开始，每页size条）
        Pageable pageable = PageRequest.of(page, size);

        // 2. 基础查询：按店铺名称或无条件分页查询
        Page<Shop> shopPage;
        if (StringUtils.hasText(shopNameKeyword)) {
            // 按名称关键词分页查询
            shopPage = shopRepository.findByNameContaining(shopNameKeyword, pageable);
        } else {
            // 无条件分页查询所有店铺
            shopPage = shopRepository.findAll(pageable);
        }

        // 3. 如果有剧院关键词，筛选与符合条件的剧院关联的店铺
        if (StringUtils.hasText(theaterKeyword)) {
            // 3.1 查找到符合条件的剧院
            List<Theater> matchedTheaters = theaterRepository
                    .findByNameContainingOrLocationNameContaining(theaterKeyword, theaterKeyword);
            Set<Long> matchedTheaterIds = matchedTheaters.stream()
                    .map(Theater::getId)
                    .collect(Collectors.toSet());

            // 3.2 获取这些剧院关联的所有店铺ID
            Set<Long> relatedShopIds = theaterShopRepository.findAll().stream()
                    .filter(ts -> matchedTheaterIds.contains(ts.getTheaterId()))
                    .map(TheaterShop::getShopId)
                    .collect(Collectors.toSet());

            // 3.3 如果没有关联店铺，直接返回空分页
            if (relatedShopIds.isEmpty()) {
                return new PageImpl<>(Collections.emptyList(), pageable, 0);
            }

            // 3.4 基于关联店铺ID分页查询（避免内存分页，直接在数据库层面筛选）
            // 注意：如果同时有店铺名称关键词，需先筛选名称再筛选ID
            Page<Shop> filteredPage;
            if (StringUtils.hasText(shopNameKeyword)) {
                // 先按名称查询，再筛选ID（利用原有shopPage的内容ID）
                Set<Long> nameFilteredIds = shopPage.getContent().stream()
                        .map(Shop::getId)
                        .collect(Collectors.toSet());
                relatedShopIds.retainAll(nameFilteredIds); // 取交集

                filteredPage = shopRepository.findByIdIn(relatedShopIds, pageable);
            } else {
                // 直接按关联ID分页查询
                filteredPage = shopRepository.findByIdIn(relatedShopIds, pageable);
            }

            // 3.5 转换为DTO并返回分页结果
            List<ShopWithTheatersDTO> dtoList = filteredPage.getContent().stream()
                    .map(this::convertToShopWithTheatersDTO)
                    .collect(Collectors.toList());

            return new PageImpl<>(dtoList, pageable, filteredPage.getTotalElements());
        }

        // 4. 无剧院关键词：直接转换为DTO返回分页结果
        List<ShopWithTheatersDTO> dtoList = shopPage.getContent().stream()
                .map(this::convertToShopWithTheatersDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, shopPage.getTotalElements());
    }


    // 新增工具方法：将Shop转换为ShopWithTheatersDTO（提取重复逻辑）
    private ShopWithTheatersDTO convertToShopWithTheatersDTO(Shop shop) {
        ShopWithTheatersDTO dto = new ShopWithTheatersDTO();
        dto.setShop(shop);

        // 获取关联的剧院
        List<TheaterShop> theaterShops = theaterShopRepository.findByShopId(shop.getId());
        List<Long> theaterIds = theaterShops.stream()
                .map(TheaterShop::getTheaterId)
                .collect(Collectors.toList());
        dto.setTheaterIds(theaterIds);

        List<Theater> theaters = theaterRepository.findByTheaterIds(theaterIds);
        dto.setTheaters(theaters);

        return dto;
    }


    /**
     * 获取店铺及其关联的剧院
     * @param shopId 店铺ID
     * @return 包含关联剧院的店铺信息
     */
    @Transactional
    @Override
    public ShopWithTheatersDTO getShopWithTheaters(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("店铺不存在"));

        ShopWithTheatersDTO dto = new ShopWithTheatersDTO();
        dto.setShop(shop);

        // 获取关联的剧院ID
        List<TheaterShop> theaterShops = theaterShopRepository.findByShopId(shopId);
        List<Long> theaterIds = theaterShops.stream()
                .map(TheaterShop::getTheaterId)
                .collect(Collectors.toList());
        dto.setTheaterIds(theaterIds);

        // 获取关联的剧院详细信息
        List<Theater> theaters = theaterRepository.findByTheaterIds(theaterIds);
        dto.setTheaters(theaters);

        return dto;
    }

    /**
     * 添加店铺
     * @param shopWithTheaters 包含店铺信息和关联剧院ID的DTO
     * @return 新增的店铺
     */
    @Transactional
    @Override
    public Shop addShop(ShopWithTheatersDTO shopWithTheaters) {
        Shop shop = shopWithTheaters.getShop();
        List<Long> theaterIds = shopWithTheaters.getTheaterIds();

        // 验证：店铺名称和高德ID不能重复
        if (shopRepository.findByName(shop.getName()).isPresent()) {
            throw new RuntimeException("店铺名称已存在");
        }

        if (StringUtils.hasText(shop.getGaodeId()) &&
                shopRepository.findByGaodeId(shop.getGaodeId()).isPresent()) {
            throw new RuntimeException("高德ID已存在");
        }

        // 验证：至少关联一个剧院
        if (theaterIds == null || theaterIds.isEmpty()) {
            throw new RuntimeException("至少需要关联一个剧院");
        }

        // 保存店铺
        Shop savedShop = shopRepository.save(shop);

        // 保存关联关系
        for (Long theaterId : theaterIds) {
            // 检查剧院是否存在
            if (!theaterRepository.existsById(theaterId)) {
                // 回滚事务
                throw new RuntimeException("剧院ID不存在: " + theaterId);
            }

            TheaterShop theaterShop = new TheaterShop();
            theaterShop.setTheaterId(theaterId);
            theaterShop.setShopId(savedShop.getId());
            theaterShopRepository.save(theaterShop);
        }

        return savedShop;
    }

    /**
     * 更新店铺
     * @param shopId 店铺ID
     * @param shopWithTheaters 包含更新信息的DTO
     * @return 更新后的店铺
     */
    @Transactional
    @Override
    public Shop updateShop(Long shopId, ShopWithTheatersDTO shopWithTheaters) {
        // 检查店铺是否存在
        Shop existingShop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("店铺不存在"));

        Shop updatedShop = shopWithTheaters.getShop();
        List<Long> theaterIds = shopWithTheaters.getTheaterIds();

        // 验证：店铺名称不能与其他店铺重复
        if (!existingShop.getName().equals(updatedShop.getName())) {
            if (shopRepository.findByName(updatedShop.getName()).isPresent()) {
                throw new RuntimeException("店铺名称已存在");
            }
        }

        // 验证：高德ID不能与其他店铺重复
        if (StringUtils.hasText(updatedShop.getGaodeId()) &&
                !updatedShop.getGaodeId().equals(existingShop.getGaodeId()) &&
                shopRepository.findByGaodeId(updatedShop.getGaodeId()).isPresent()) {
            throw new RuntimeException("高德ID已存在");
        }

        // 验证：至少关联一个剧院
        if (theaterIds == null || theaterIds.isEmpty()) {
            throw new RuntimeException("至少需要关联一个剧院");
        }

        // 更新店铺信息
        existingShop.setName(updatedShop.getName());
        existingShop.setAddress(updatedShop.getAddress());
        existingShop.setCategory(updatedShop.getCategory());
        existingShop.setImage(updatedShop.getImage());
        existingShop.setGaodeId(updatedShop.getGaodeId());

        Shop savedShop = shopRepository.save(existingShop);

        // 删除原有关联关系
        theaterShopRepository.deleteByShopId(shopId);

        // 添加新的关联关系
        for (Long theaterId : theaterIds) {
            if (!theaterRepository.existsById(theaterId)) {
                throw new RuntimeException("剧院ID不存在: " + theaterId);
            }

            TheaterShop theaterShop = new TheaterShop();
            theaterShop.setTheaterId(theaterId);
            theaterShop.setShopId(savedShop.getId());
            theaterShopRepository.save(theaterShop);
        }

        return savedShop;
    }

    /**
     * 删除店铺
     * @param shopId 店铺ID
     */
    @Transactional
    @Override
    public void deleteShop(Long shopId) {
        // 检查店铺是否存在
        if (!shopRepository.existsById(shopId)) {
            throw new RuntimeException("店铺不存在");
        }

        // 删除关联关系
        theaterShopRepository.deleteByShopId(shopId);

        // 删除店铺
        shopRepository.deleteById(shopId);
    }

    /**
     * 获取所有剧院（用于下拉选择）
     * @return 所有剧院列表
     */
    @Override
    public List<Theater> getAllTheaters() {
        return theaterRepository.findAll();
    }
}
