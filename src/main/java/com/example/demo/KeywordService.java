package com.example.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private String baseUrl = "https://api.searchad.naver.com";
    private String path = "/keywordstool";
    private String accessKey = "0100000000458559f27d45c631ea18a06d587bc58247a208a31ccd22352409b08ee7e87df8"; // 액세스키
    private String secretKey = "AQAAAAASXKsydy3KucAlNHgUvjLtT4XLHQUbTt8FRoT4kwRl/A==";  // 시크릿키
    private String customerId = "3196241";  // ID

    public String requestKeyword(String keyword) {

        String parameter = "hintKeywords=";
        long timeStamp = System.currentTimeMillis();
        URL url = null;
        String times = String.valueOf(timeStamp);

        try {
            keyword = URLEncoder.encode(keyword, "UTF-8");
            //keyword = URLEncoder.encode(keyword, "EUC-KR");
            System.out.println("keyword : "+keyword);
        } catch (Exception e) {
            throw new RuntimeException("인코딩 실패.");
        }

        try {
            url = new URL(baseUrl+path+"?"+parameter+keyword);
            System.out.println("url : "+url);
            String signature = Signatures.of(times,  "GET", path, secretKey);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("X-Timestamp", times);
            con.setRequestProperty("X-API-KEY", accessKey);
            con.setRequestProperty("X-Customer", customerId);
            con.setRequestProperty("X-Signature", signature);
            con.setDoOutput(true);

            int responseCode = con.getResponseCode();
            BufferedReader br;
            System.out.println("responseCode : "+responseCode);
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
            //System.out.println("Response : "+response.toString());
            return response.toString();

        } catch (Exception e) {
            System.out.println("Wrong URL." + e.getMessage());
        }
        return "";
    }
    public String getKeywordData(String keyword) {
        String jsonResponse = requestKeyword(keyword);
        //System.out.println(keyword);
        if (jsonResponse == null) {
            return "{}";
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode keywordList = rootNode.get("keywordList");
            if (keywordList.isArray()) {
                for (JsonNode keywordNode : keywordList) {
                    String relKeyword = keywordNode.get("relKeyword").asText();
                    if (keyword.equals(relKeyword)) {
                        int monthlyPcQcCnt = keywordNode.get("monthlyPcQcCnt").asInt();
                        int monthlyMobileQcCnt = keywordNode.get("monthlyMobileQcCnt").asInt();
                        return String.format("{\"relKeyword\":\"%s\",\"monthlyPcQcCnt\":%d,\"monthlyMobileQcCnt\":%d}", relKeyword, monthlyPcQcCnt, monthlyMobileQcCnt);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("JSON Parsing Error: " + e.getMessage());
        }

        return "{}"; // "제주도" 키워드가 없으면 빈 JSON 객체 반환
    }
}
