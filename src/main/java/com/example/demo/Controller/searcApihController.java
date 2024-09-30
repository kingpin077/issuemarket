package com.example.demo.Controller;

import com.example.demo.Service.KeywordService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
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
public class searcApihController {      //키워드 검색을 다루는 컨트롤러

    private final String NAVER_API_ID = "SaxdyV1a_KAviNQhXO5Z";
    private final String NAVER_API_SECRET = "21k6H8Ga3k";
    KeywordService ks = new KeywordService();   // 키워드 서비스 객체 생성

    // POST 요청으로 키워드를 입력받아 검색 처리
    @PostMapping("/search")     //검색 키워드를 입력받음
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam(required = false) String groupName,
            @RequestParam(required = false) String keywords
    ) {
        // 현재 날짜 기준으로 한 달 전부터 오늘까지의 날짜 범위 설정
        LocalDate endDate = LocalDate.now();                        //오늘 날짜를 받아옴
        LocalDate startDate = endDate.minusMonths(1);// 한 달 전 날짜

        // 날짜 포맷 지정 (yyyy-MM-dd 형식)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedEndDate = endDate.format(formatter);
        String formattedStartDate = startDate.format(formatter);

        String requestBody = String.format("{\"startDate\":\"%s\",\"endDate\":\"%s\",\"timeUnit\":\"month\",\"keywordGroups\":[{\"groupName\":\"%s\",\"keywords\":[\"%s\"]}],\"device\":\"mo\",\"gender\":\"f\"}",
                formattedStartDate, formattedEndDate, groupName, keywords);     //api 호출 body

        // HTTP 헤더 설정 (JSON 형식 및 네이버 API 인증 정보)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Naver-Client-Id", NAVER_API_ID);
        headers.set("X-Naver-Client-Secret", NAVER_API_SECRET);

        // HTTP 요청 엔티티 생성 (요청 본문과 헤더 포함)
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        // RestTemplate 객체 생성 (HTTP 요청을 보낼 수 있는 객체)
        RestTemplate restTemplate = new RestTemplate();

        // 네이버 데이터랩 검색 API 호출 (POST 요청)
        ResponseEntity<String> response = restTemplate.exchange(
                "https://openapi.naver.com/v1/datalab/search",
                HttpMethod.POST,
                requestEntity,
                String.class);

        // 결과를 저장할 Map 생성
        Map<String, Object> result = new HashMap<>();
        result.put("naverData", response.getBody());
        result.put("kakaoData", getKeywordRatio(keywords).getBody());

        // KeywordService에서 추가 데이터 가져오기
        String keywordData = ks.getKeywordData(keywords);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 가져온 데이터 파싱 및 결과에 추가
            JsonNode keywordJson = objectMapper.readTree(keywordData);
            result.put("monthlyPcQcCnt", keywordJson.get("monthlyPcQcCnt").asInt());    // PC 검색량
            result.put("monthlyMobileQcCnt", keywordJson.get("monthlyMobileQcCnt").asInt());    // 모바일 검색량
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(result);   // 결과 반환
    }

    // 카카오 검색 트렌드 데이터를 가져오는 메서드
    public ResponseEntity<Map<String, Object>> getKeywordRatio(String keyword){
        RestTemplate restTemplate = new RestTemplate();  // RestTemplate 객체 생성

        // 한 달 전부터 어제까지의 날짜 범위 설정
        LocalDate endDate = LocalDate.now().minusDays(1);
        LocalDate startDate = endDate.minusMonths(1);

        // 날짜 포맷 지정 (yyyyMMdd 형식)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedEndDate = endDate.format(formatter);
        String formattedStartDate = startDate.format(formatter);

        // API 호출을 위한 쿼리 문자열 생성
        String q, device;
        q = keyword;
        device = "m";

        // 카카오 데이터 트렌드 API URL 생성
        String url = String.format("https://datatrend.kakao.com/api/search/trend?q=%s&from=%s&to=%s&device=%s", q, formattedStartDate, formattedEndDate, device);

        // API 호출하여 응답을 Map으로 받음
        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response == null) {
            return ResponseEntity.status(500).body(Collections.singletonMap("error", "No response from the API"));
        }

        Map<String, Object> status = (Map<String, Object>) response.get("status");
        if (status == null || (int) status.get("code") != 200) {
            return ResponseEntity.status(500).body(Collections.singletonMap("error", "Invalid response from the API"));
        }

        // 응답에서 트렌드 데이터 리스트 추출
        ArrayList<Map<String, Object>> list = (ArrayList<Map<String, Object>>) response.get("list");
        if (list == null || list.isEmpty()) {
            return ResponseEntity.status(500).body(Collections.singletonMap("error", "No list data in the response"));
        }

        // 첫 번째 아이템에서 성별 및 연령대 비율 추출
        Map<String, Object> item = list.get(0);
        Map<String, Double> gender = (Map<String, Double>) item.get("gender");  // 성별 비율
        Map<String, Double> age = (Map<String, Double>) item.get("age");     // 연령대 비율

        Map<String, Object> result = new HashMap<>();
        result.put("gender", gender);   // 성별 비율 추가
        result.put("age", age); // 연령대 비율 추가

        return ResponseEntity.ok(result);   // 결과 반환
    }


}