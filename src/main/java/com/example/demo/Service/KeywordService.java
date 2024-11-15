package com.example.demo.Service;

import com.example.demo.Signatures;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Service
@RequiredArgsConstructor
public class KeywordService {

    // 네이버 광고 API 호출에 필요한 기본 정보들
    private String baseUrl = "https://api.searchad.naver.com";
    private String path = "/keywordstool";
    private String accessKey = "0100000000458559f27d45c631ea18a06d587bc58247a208a31ccd22352409b08ee7e87df8"; // 액세스키
    private String secretKey = "AQAAAAASXKsydy3KucAlNHgUvjLtT4XLHQUbTt8FRoT4kwRl/A==";  // 시크릿키
    private String customerId = "3196241";  // ID

    // 특정 키워드에 대한 네이버 광고 API 요청을 처리하는 메서드
    public String requestKeyword(String keyword) {

        String parameter = "hintKeywords=";
        long timeStamp = System.currentTimeMillis();     // 현재 시간을 타임스탬프로 가져옴
        URL url = null;     // URL 객체 초기화
        String times = String.valueOf(timeStamp);   // 타임스탬프를 문자열로 변환

        // 키워드를 URL 인코딩 처리 (특수 문자를 안전하게 변환)
        try {
            keyword = URLEncoder.encode(keyword, "UTF-8");
            //keyword = URLEncoder.encode(keyword, "EUC-KR");
            System.out.println("keyword : "+keyword);
        } catch (Exception e) {
            throw new RuntimeException("인코딩 실패.");
        }

        // API 호출 URL 생성 및 요청 처리
        try {
            // 네이버 광고 API 요청 URL 생성
            url = new URL(baseUrl+path+"?"+parameter+keyword);
            System.out.println("url : "+url);

            // 요청 서명을 생성 (API 보안 요구 사항에 맞게 서명 생성)
            String signature = Signatures.of(times,  "GET", path, secretKey);

            // HTTP 연결 객체 생성
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // HTTP 요청 설정
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Timestamp", times);
            con.setRequestProperty("X-API-KEY", accessKey);
            con.setRequestProperty("X-Customer", customerId);
            con.setRequestProperty("X-Signature", signature);
            con.setDoOutput(true);

            int responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            }
            else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream(), "UTF-8"));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            return response.toString();

        } catch (Exception e) {
            System.out.println("Wrong URL." + e.getMessage());
        }
        return "";
    }

    // 키워드 데이터를 가져오는 메서드
    public String getKeywordData(String keyword) {
        // API로부터 받은 JSON 응답을 가져옴
        String jsonResponse = requestKeyword(keyword);
        System.out.println("Keyword List: " + jsonResponse);
        if (jsonResponse == null) {
            return "{}";
        }

        // JSON 데이터를 처리하기 위한 ObjectMapper 객체 생성
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);    // JSON 응답을 파싱하여 JsonNode 객체로 변환
            JsonNode keywordList = rootNode.get("keywordList"); // keywordList 노드 추출
            ArrayNode relatedKeywordsArray = objectMapper.createArrayNode();  // 여러 키워드를 담기 위한 배열
            ObjectNode result = objectMapper.createObjectNode();


            // 검색량 초기화
            int monthlyPcQcCnt = 0;
            int monthlyMobileQcCnt = 0;

            // keywordList가 배열일 경우 각 항목을 순회
            if (keywordList.isArray()) {
                int count = 0;
                for (JsonNode keywordNode : keywordList) {
                    String relKeyword = keywordNode.get("relKeyword").asText(); // 관련 키워드 추출

                    // 요청한 키워드와 일치하는 경우 검색량을 저장
                    if (keyword.equals(relKeyword)) {
                        monthlyPcQcCnt = keywordNode.get("monthlyPcQcCnt").asInt();
                        monthlyMobileQcCnt = keywordNode.get("monthlyMobileQcCnt").asInt();
                    } else if (count < 10) {
                        // 관련 검색어로 추가
                        relatedKeywordsArray.add(relKeyword);
                        count++;
                    }
                }
            }


            // 최종 JSON 결과 생성
            result.put("relKeyword", keyword);
            result.put("monthlyPcQcCnt", monthlyPcQcCnt);
            result.put("monthlyMobileQcCnt", monthlyMobileQcCnt);
            result.set("relatedKeywords", relatedKeywordsArray);

            return result.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "{}";  // 에러 발생 시 빈 JSON 반환
        }
    }
}
