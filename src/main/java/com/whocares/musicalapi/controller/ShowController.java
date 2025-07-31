package com.whocares.musicalapi.controller;
import com.whocares.musicalapi.dto.response.ShowResponse;
import com.whocares.musicalapi.service.ShowService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shows")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ShowController {

    private final ShowService showService;

    @Autowired
    public ShowController(ShowService showService) {
        this.showService = showService;
    }

    @GetMapping
    public ResponseEntity<List<ShowResponse>> getAllShows() {
        List<ShowResponse> shows = showService.findAllShows();
        if (shows .isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(shows);
    }

    @GetMapping("/musical/{musicalId}")
    public ResponseEntity<List<ShowResponse>> getShowsByMusicalId(@PathVariable Long musicalId) {
        List<ShowResponse> shows = showService.getShowsByMusicalId(musicalId);
        return ResponseEntity.ok(shows);
    }

    @GetMapping("/theater/{theaterId}")
    public ResponseEntity<List<ShowResponse>> getShowsByTheaterId(@PathVariable Long theaterId) {
        List<ShowResponse> shows = showService.getShowsByTheaterId(theaterId);
        return ResponseEntity.ok(shows);
    }

    @GetMapping("/{year}/{month}")
    public List<ShowResponse> getShowsByYearAndMonth(@PathVariable int year, @PathVariable int month) {
        return showService.getShowsByYearAndMonth(year, month);
    }



}
