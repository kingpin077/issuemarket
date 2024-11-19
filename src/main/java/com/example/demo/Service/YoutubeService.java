package com.example.demo.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class YoutubeService {
    private final String YOUTUBE_API_KEY = "AIzaSyADzbIlMdc7CWn6NkzTttfh_FlSP1c3-TU"; // YouTube API 키
    public Map<String, Object> getYoutubeVideo(String keyword) {
        String url = String.format("https://www.googleapis.com/youtube/v3/search?part=snippet&order=viewCount&type=video&q=%s&key=%s", keyword, YOUTUBE_API_KEY);
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> result = new HashMap<>();
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode items = rootNode.get("items");
            if (items != null && items.isArray() && items.size() > 0) {
                JsonNode video = items.get(0); // 조회수가 가장 높은 영상
                String videoId = video.get("id").get("videoId").asText();
                String videoUrl = "https://www.youtube.com/watch?v=" + videoId;
                // 조회수 정보 가져오기
                String videoDetailsUrl = String.format("https://www.googleapis.com/youtube/v3/videos?part=statistics&id=%s&key=%s", videoId, YOUTUBE_API_KEY);
                ResponseEntity<String> videoDetailsResponse = restTemplate.getForEntity(videoDetailsUrl, String.class);
                JsonNode videoDetailsRoot = objectMapper.readTree(videoDetailsResponse.getBody());
                int viewCount = videoDetailsRoot.get("items").get(0).get("statistics").get("viewCount").asInt();
                result.put("videoUrl", videoUrl);
                result.put("viewCount", viewCount);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("error", "Failed to fetch YouTube video data");
        }
        return result;
    }
    public List<Map<String, String>> getTrendingVideos() {
        String url = String.format("https://www.googleapis.com/youtube/v3/videos?part=snippet&chart=mostPopular&maxResults=10&regionCode=KR&key=%s", YOUTUBE_API_KEY);
        RestTemplate restTemplate = new RestTemplate();
        List<Map<String, String>> trendingVideos = new ArrayList<>();
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode items = rootNode.get("items");
            if (items != null && items.isArray()) {
                for (JsonNode video : items) {
                    String title = video.get("snippet").get("title").asText();
                    String videoId = video.get("id").asText();
                    String videoUrl = "https://www.youtube.com/watch?v=" + videoId;
                    Map<String, String> videoData = new HashMap<>();
                    videoData.put("title", title);
                    videoData.put("url", videoUrl);
                    trendingVideos.add(videoData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trendingVideos;
    }
}