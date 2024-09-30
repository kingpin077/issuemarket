package com.example.demo.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
public class NewsApiController {    //네이버 뉴스API

    private final String NAVER_API_ID = "SaxdyV1a_KAviNQhXO5Z";
    private final String NAVER_API_SECRET = "21k6H8Ga3k";

    @GetMapping("/news")
    public ResponseEntity<Map<String, Object>> search(  //아무런 값이 들어오지 않으면 아래 RequestParam을 통해 기본값을 설정
            @RequestParam(required = false, defaultValue = "주식") String query,
            @RequestParam(required = false, defaultValue = "3") String display,
            @RequestParam(required = false, defaultValue = "1") String start,
            @RequestParam(required = false, defaultValue = "sim") String sort
    ) {
        String url = "https://openapi.naver.com/v1/search/news.json?query=" + query + "&display=" + display + "&start=" + start + "&sort=" + sort;  //api 호출 url

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Naver-Client-Id", NAVER_API_ID);
        headers.set("X-Naver-Client-Secret", NAVER_API_SECRET);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                String.class);

        Map<String, Object> result = new HashMap<>();
        result.put("naverData", response.getBody());
        System.out.println(response.getBody());

        ObjectMapper objectMapper = new ObjectMapper();

        return ResponseEntity.ok(result);
    }
}
