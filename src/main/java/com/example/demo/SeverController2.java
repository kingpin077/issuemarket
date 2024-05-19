package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class SeverController2 {

    private final String NAVER_API_ID = "SaxdyV1a_KAviNQhXO5Z";
    private final String NAVER_API_SECRET = "21k6H8Ga3k";
    KeywordService ks = new KeywordService();
    String sa;

    // http://localhost:8888/api/server/naver?startDate=2024-05-07&endDate=2024-05-08&groupName=범죄도시&keywords=범죄도시&device=mo&gender=f
    //http://localhost:8080/naver2?startDate=2024-03-07&endDate=2024-05-08&groupName=%EB%B2%94%EC%A3%84%EB%8F%84%EC%8B%9C&keywords=%EB%B2%94%EC%A3%84%EB%8F%84%EC%8B%9C
    @GetMapping("/search")
    public ResponseEntity<String> search(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String groupName,
            @RequestParam String keywords
    ) { //,"gender":"f"
        String requestBody = String.format("{\"startDate\":\"2024-03-07\",\"endDate\":\"2024-05-18\",\"timeUnit\":\"month\",\"keywordGroups\":[{\"groupName\":\"%s\",\"keywords\":[\"%s\"]}],\"device\":\"mo\"}",
                groupName, keywords);

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

        //sa = ks.getKeywordData(keywords);

        return response;
    }
}