package com.whocares.musicalapi.controller.admin;


import com.whocares.musicalapi.entity.News;
import com.whocares.musicalapi.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/news")
public class AdminNewsController {

    @Autowired
    private NewsService newsService;

    @GetMapping
    public List<News> getAllNews() {
        return newsService.getAllNews();
    }

    @GetMapping("/{id}")
    public ResponseEntity<News> getNewsById(@PathVariable Long id) {
        try {
            News news = newsService.getNewsById(id);
            if (news != null) {
                return ResponseEntity.ok(news);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            // 日志记录异常
            System.err.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping
    public News createNews(@RequestBody News news) {
        return newsService.saveNews(news);
    }

    @PutMapping("/{id}")
    public ResponseEntity<News> updateNews(@PathVariable Long id, @RequestBody News news) {
        News existingNews = newsService.getNewsById(id);
        if (existingNews != null) {
            existingNews.setTitle(news.getTitle());
            existingNews.setDate(news.getDate());
            existingNews.setSummary(news.getSummary());
            return ResponseEntity.ok(newsService.saveNews(existingNews));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
        return ResponseEntity.noContent().build();
    }
}