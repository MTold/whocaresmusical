package com.whocares.musicalapi.controller;


import com.whocares.musicalapi.entity.Shop;
import com.whocares.musicalapi.entity.Theater;
import com.whocares.musicalapi.service.TheaterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

//表示这是一个 RESTful Web 服务控制器。它是 @Controller和@ResponseBody 的组合注解。所有返回值都会自动序列化为 JSON（或其他格式）并直接写入 HTTP 响应体中。
@RestController
@RequestMapping("/api/theaters")
@CrossOrigin(origins = "*", maxAge = 3600) //用于处理 跨域资源共享（CORS）的问题
public class TheaterController {

    @Autowired
    private final TheaterService theaterService;

    public TheaterController(TheaterService theaterService) {
        this.theaterService = theaterService;
    }

    //返回所有剧院
    @GetMapping
    public ResponseEntity<List<Theater>> getAllTheaters() {
        List<Theater> theaterResponses = theaterService.findAllTheaters();
        return ResponseEntity.ok(theaterResponses);
    }

    //创建新剧院
    @PostMapping
    public ResponseEntity<Long> createTheater(@RequestBody Theater theater){
        Long createdTheater=theaterService.saveOrUpdateTheater(theater);
        return ResponseEntity.ok(createdTheater);
    }

    //更新剧院
    @PutMapping("/{id}")
    public ResponseEntity<Long> updateTheater(@PathVariable Long id,@RequestBody Theater theater){
        theater.setId(id);
        Long updatedTheater=theaterService.saveOrUpdateTheater(theater);
        return  ResponseEntity.ok(updatedTheater);
    }

    //删除音乐剧
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheater(@PathVariable Long id){
        theaterService.deleteTheaterById(id);
        return ResponseEntity.noContent().build();
    }

    //根据id返回剧院
    @GetMapping("/{id}")
    public ResponseEntity<Theater> getTheaterById(@PathVariable Long id) {
        Theater theater = theaterService.findTheaterById(id);
        return ResponseEntity.ok(theater);
    }

    //根据剧院查找店铺
    @GetMapping("/{theaterId}/shops")
    public ResponseEntity<List<Shop>> getShopsByTheaterId(@PathVariable Long theaterId) {
        List<Shop> shops = theaterService.findShopsByTheaterId(theaterId);
        return ResponseEntity.ok(shops);
    }

    //根据剧院和类别查找店铺
    @GetMapping("/{theaterId}/shops/{category}")
    public ResponseEntity<List<Shop>> getShopsByTheaterIdAndCategory(@PathVariable Long theaterId , @PathVariable Integer category) {
        List<Shop> shops = theaterService.findShopsByTheaterIdAndCategory(theaterId , category);
        return ResponseEntity.ok(shops);
    }

}
