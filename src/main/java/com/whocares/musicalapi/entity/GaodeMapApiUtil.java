package com.whocares.musicalapi.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class GaodeMapApiUtil {

    @Value("${gaode.map.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // 高德地图周边搜索API
    private static final String AROUND_SEARCH_URL = "https://restapi.amap.com/v5/place/around";

    /**
     * 搜索周边店铺
     * @param longitude 经度
     * @param latitude 纬度
     * @param radius 搜索半径(米)
     * @param types 店铺类型编码
     * @return 店铺数据JSON
     */
    public JSONArray searchAroundShops(double longitude, double latitude, int radius, String types) {
        try {
            // 构建请求URL
            StringBuilder urlBuilder = new StringBuilder(AROUND_SEARCH_URL);
            urlBuilder.append("?location=").append(longitude).append(",").append(latitude);
            urlBuilder.append("&radius=").append(radius);
            urlBuilder.append("&types=").append(URLEncoder.encode(types, StandardCharsets.UTF_8.name()));
            urlBuilder.append("&key=").append(apiKey);
            urlBuilder.append("&show_fields=business,photos");

            // 发送请求
            String response = restTemplate.getForObject(urlBuilder.toString(), String.class);

            // 解析响应
            JSONObject jsonObject = JSONObject.parseObject(response);
            if ("1".equals(jsonObject.getString("status")) && "OK".equals(jsonObject.getString("info"))) {
                return jsonObject.getJSONArray("pois");
            } else {
                // 处理API错误
                System.err.println("高德地图API错误: " + jsonObject.getString("info"));
                return new JSONArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }
}
