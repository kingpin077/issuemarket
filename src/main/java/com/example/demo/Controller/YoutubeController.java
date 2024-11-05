package com.example.demo.Controller;

import com.example.demo.Service.YoutubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class YoutubeController {

    private final YoutubeService youtubeService;

    @Autowired
    public YoutubeController(YoutubeService youtubeService) {
        this.youtubeService = youtubeService;
    }

    @GetMapping("/youtube/search")
    public ResponseEntity<Map<String, Object>> getYoutubeVideo(@RequestParam String keyword) {
        Map<String, Object> youtubeData = youtubeService.getYoutubeVideo(keyword);
        return ResponseEntity.ok(youtubeData);
    }

    @GetMapping("/youtube/trending")
    public ResponseEntity<List<Map<String, String>>> getTrendingVideos() {
        List<Map<String, String>> trendingVideos = youtubeService.getTrendingVideos();
        return ResponseEntity.ok(trendingVideos);
    }
}
