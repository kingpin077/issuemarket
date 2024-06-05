package com.example.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
public class SeverController2 {

    private final String NAVER_API_ID = "SaxdyV1a_KAviNQhXO5Z";
    private final String NAVER_API_SECRET = "21k6H8Ga3k";
    KeywordService ks = new KeywordService();
    String sa;

    //https://datatrend.kakao.com/api/search/trend?q=고혼진&from=20240422&to=20240522&device=m
    // http://localhost:8888/api/server/naver?startDate=2024-05-07&endDate=2024-05-08&groupName=범죄도시&keywords=범죄도시&device=mo&gender=f
    //http://localhost:8080/naver2?startDate=2024-03-07&endDate=2024-05-08&groupName=%EB%B2%94%EC%A3%84%EB%8F%84%EC%8B%9C&keywords=%EB%B2%94%EC%A3%84%EB%8F%84%EC%8B%9C
    @PostMapping("/search")
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam(required = false) String groupName,
            @RequestParam(required = false) String keywords
    ) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedEndDate = endDate.format(formatter);
        String formattedStartDate = startDate.format(formatter);

        String requestBody = String.format("{\"startDate\":\"%s\",\"endDate\":\"%s\",\"timeUnit\":\"month\",\"keywordGroups\":[{\"groupName\":\"%s\",\"keywords\":[\"%s\"]}],\"device\":\"mo\",\"gender\":\"f\"}",
                formattedStartDate, formattedEndDate, groupName, keywords);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Naver-Client-Id", NAVER_API_ID);
        headers.set("X-Naver-Client-Secret", NAVER_API_SECRET);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(
                "https://openapi.naver.com/v1/datalab/search",
                HttpMethod.POST,
                requestEntity,
                String.class);

        Map<String, Object> result = new HashMap<>();
        result.put("naverData", response.getBody());
        result.put("kakaoData", getKeywordRatio(keywords).getBody());

        String keywordData = ks.getKeywordData(keywords);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode keywordJson = objectMapper.readTree(keywordData);
            result.put("monthlyPcQcCnt", keywordJson.get("monthlyPcQcCnt").asInt());
            result.put("monthlyMobileQcCnt", keywordJson.get("monthlyMobileQcCnt").asInt());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(result);
    }

    public ResponseEntity<Map<String, Object>> getKeywordRatio(String keyword){
        RestTemplate restTemplate = new RestTemplate();

        String q, from, to, device;
        q = keyword;
        from = "20240422";
        to = "20240522";
        device = "m";

        String url = String.format("https://datatrend.kakao.com/api/search/trend?q=%s&from=%s&to=%s&device=%s", q, from, to, device);

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response == null) {
            return ResponseEntity.status(500).body(Collections.singletonMap("error", "No response from the API"));
        }

        Map<String, Object> status = (Map<String, Object>) response.get("status");
        if (status == null || (int) status.get("code") != 200) {
            return ResponseEntity.status(500).body(Collections.singletonMap("error", "Invalid response from the API"));
        }

        ArrayList<Map<String, Object>> list = (ArrayList<Map<String, Object>>) response.get("list");
        if (list == null || list.isEmpty()) {
            return ResponseEntity.status(500).body(Collections.singletonMap("error", "No list data in the response"));
        }

        Map<String, Object> item = list.get(0);
        Map<String, Double> gender = (Map<String, Double>) item.get("gender");
        Map<String, Double> age = (Map<String, Double>) item.get("age");

        Map<String, Object> result = new HashMap<>();
        result.put("gender", gender);
        result.put("age", age);

        return ResponseEntity.ok(result);
    }
}