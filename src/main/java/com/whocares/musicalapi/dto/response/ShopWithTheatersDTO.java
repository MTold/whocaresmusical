package com.whocares.musicalapi.dto.response;

import com.whocares.musicalapi.entity.Shop;
import com.whocares.musicalapi.entity.Theater;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopWithTheatersDTO {

    private Shop shop;
    private List<Long> theaterIds; // 关联的剧院ID列表
    private List<Theater> theaters; // 关联的剧院详细信息（用于查询返回）

    public ShopWithTheatersDTO(Shop shop, List<Long> theaterIds) {
        this.shop = shop;
        this.theaterIds = theaterIds;
    }
    //是否需要给theaters赋值？？？

}
