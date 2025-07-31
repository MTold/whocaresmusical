package com.whocares.musicalapi.service;

import com.whocares.musicalapi.dto.response.ShopWithTheatersDTO;
import com.whocares.musicalapi.entity.Review;
import com.whocares.musicalapi.entity.Shop;
import com.whocares.musicalapi.entity.Theater;
import com.whocares.musicalapi.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface ShopService {

    String syncAllTheaterShops();

    String syncSingleTheaterShops(Long theaterId);

    /**搜索店铺
     * @param shopNameKeyword 店铺名称关键词
     * @param theaterKeyword 剧院名称或位置关键词
     * @return 符合条件的店铺列表 */
    Page<ShopWithTheatersDTO> searchShops(String shopNameKeyword, String theaterKeyword, int page, int size);

    /**获取店铺及其关联的剧院
     * @param shopId 店铺ID
     * @return 包含关联剧院的店铺信息 */
    ShopWithTheatersDTO getShopWithTheaters(Long shopId);

    /**添加店铺
     * @param shopWithTheaters 包含店铺信息和关联剧院ID的DTO
     * @return 新增的店铺 */
    Shop addShop(ShopWithTheatersDTO shopWithTheaters);

    /**更新店铺
     * @param shopId 店铺ID
     * @param shopWithTheaters 包含更新信息的DTO
     * @return 更新后的店铺 */
    Shop updateShop(Long shopId, ShopWithTheatersDTO shopWithTheaters);

    /**删除店铺
     * @param shopId 店铺ID */
    void deleteShop(Long shopId);

    /**获取所有剧院（用于下拉选择）
     * @return 所有剧院列表 */
    List<Theater> getAllTheaters();
}