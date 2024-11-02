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
    @PostMapping("/search")  // 검색 키워드를 입력받음
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam(required = false) String groupName,
            @RequestParam(required = false) String keywords
    ) {
        // 현재 날짜 기준으로 한 달 전부터 오늘까지의 날짜 범위 설정
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(6);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedEndDate = endDate.format(formatter);
        String formattedStartDate = startDate.format(formatter);

        String requestBody = String.format("{\"startDate\":\"%s\",\"endDate\":\"%s\",\"timeUnit\":\"month\",\"keywordGroups\":[{\"groupName\":\"%s\",\"keywords\":[\"%s\"]}],\"gender\":\"f\"}",
                formattedStartDate, formattedEndDate, groupName, keywords);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Naver-Client-Id", NAVER_API_ID);
        headers.set("X-Naver-Client-Secret", NAVER_API_SECRET);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();

        // 네이버 데이터랩 검색 API 호출 (POST 요청)
        ResponseEntity<String> response = restTemplate.exchange(
                "https://openapi.naver.com/v1/datalab/search",
                HttpMethod.POST,
                requestEntity,
                String.class);

        Map<String, Object> result = new HashMap<>();
        result.put("naverData", response.getBody());
        result.put("kakaoData", getKeywordRatio(keywords).getBody());

        // KeywordService에서 추가 데이터 가져오기
        String keywordData = ks.getKeywordData(keywords);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode keywordJson = objectMapper.readTree(keywordData);
            int monthlyPcQcCnt = keywordJson.get("monthlyPcQcCnt").asInt();  // PC 검색량
            int monthlyMobileQcCnt = keywordJson.get("monthlyMobileQcCnt").asInt();  // 모바일 검색량
            int total = monthlyPcQcCnt + monthlyMobileQcCnt;  // Total 계산

            // 네이버 데이터 (JSON)을 파싱하여 ratio 값 계산
            JsonNode naverDataJson = objectMapper.readTree(response.getBody());
            JsonNode results = naverDataJson.get("results").get(0).get("data");
            if (results != null && results.isArray()) {
                // 이번 달 ratio
                double currentMonthRatio = results.get(results.size() - 1).get("ratio").asDouble();
                double x = total / (currentMonthRatio / 100);  // x 계산 (수식 수정됨)

                // 각 달의 ratio * x 값 계산
                ArrayList<Map<String, Object>> ratioResults = new ArrayList<>();
                for (JsonNode resultNode : results) {
                    String period = resultNode.get("period").asText();
                    double ratio = resultNode.get("ratio").asDouble();
                    double estimatedValue = Double.parseDouble(String.format("%.1f", (ratio * x / 100)));// 소수점 첫째 자리에서 반올림

                    Map<String, Object> ratioResult = new HashMap<>();
                    ratioResult.put("period", period);
                    ratioResult.put("estimatedValue", estimatedValue);
                    ratioResults.add(ratioResult);
                }

                result.put("ratioResults", ratioResults);  // 계산된 값 추가
            }

            result.put("monthlyPcQcCnt", monthlyPcQcCnt);
            result.put("monthlyMobileQcCnt", monthlyMobileQcCnt);

            // 연관 검색어 추가
            JsonNode relatedKeywords = keywordJson.get("relatedKeywords");
            if (relatedKeywords != null && relatedKeywords.isArray()) {
                ArrayList<String> relatedKeywordList = new ArrayList<>();
                for (JsonNode relatedKeyword : relatedKeywords) {
                    relatedKeywordList.add(relatedKeyword.asText());
                }
                result.put("relatedKeywords", relatedKeywordList);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(result);
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