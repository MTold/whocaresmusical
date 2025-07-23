package com.whocares.musicalapi.controller;
import com.whocares.musicalapi.entity.Show;
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
    public ResponseEntity<List<Show>> getAllShows() {
        List<Show> shows = showService.findAllShows();
        if (shows .isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(shows);
    }

    @GetMapping("/musical/{musicalId}")
    public ResponseEntity<List<Show>> getShowsByMusicalId(@PathVariable Long musicalId) {
        List<Show> shows = showService.getShowsByMusicalId(musicalId);
        return ResponseEntity.ok(shows);
    }

    @GetMapping("/theater/{theaterId}")
    public ResponseEntity<List<Show>> getShowsByTheaterId(@PathVariable Long theaterId) {
        List<Show> shows = showService.getShowsByTheaterId(theaterId);
        return ResponseEntity.ok(shows);
    }
}
