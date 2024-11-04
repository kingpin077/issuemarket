package com.example.demo.Controller;

import com.example.demo.DTO.TestDTO;
import com.example.demo.Service.TestService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class PageController {
    private final TestService testService;
    private final NewsApiController newsApiController;

    public PageController(TestService testService, NewsApiController newsApiController) {
        this.testService = testService;
        this.newsApiController = newsApiController;
    }

    @GetMapping("/index")
    public String mainPage(Model model, Model model2) {
        //키워드의 총 검색량 순위를 내림차순으로 가져옴
        List<TestDTO> keywords = testService.findAllByTotalOrderByDesc();
        System.out.println("Keywords: " + keywords); // 데이터 확인
        //키워드의 PC검색량순위를 내림차순으로 가져옴
        List<TestDTO> keywords2 = testService.findAllByPcOrderByDesc();
        System.out.println("Keywords2: " + keywords2); // 데이터 확인
        //총검색량 순위와 PC검색량 순위를 model객체에 담아서 저장 - index.html의 순위표시에 이용
        model.addAttribute("keyword", testService.findAllByTotalOrderByDesc());
        model2.addAttribute("keyword2", testService.findAllByPcOrderByDesc());
        return "index";
    }

    @GetMapping("/indexwd")
    public String mainPagewd(Model model) {
        // 키워드의 총 검색량 순위를 내림차순으로 가져옴
        List<TestDTO> keywords = testService.findAllByTotalOrderByDesc();
        System.out.println("Keywords: " + keywords); // 데이터 확인

        // 키워드의 PC 검색량 순위를 내림차순으로 가져옴
        List<TestDTO> keywords2 = testService.findAllByPcOrderByDesc();
        System.out.println("Keywords2: " + keywords2); // 데이터 확인

        // 상위 50개의 키워드를 총 검색량 기준 내림차순으로 가져옴 (wordCloud 용 데이터)
        List<TestDTO> topKeywords = testService.findTop50ByTotalOrderByDesc();
        System.out.println("Top Keywords: " + topKeywords); // 데이터 확인

        // 모델에 키워드 데이터를 저장 - index.html과 wordCloud.html에서 모두 사용 가능
        model.addAttribute("keyword", keywords);       // index.html 용
        model.addAttribute("keyword2", keywords2);     // index.html 용
        model.addAttribute("keywordCloudData", topKeywords); // wordCloud.html 용

        return "indexwd2"; // "indexwd" 페이지로 반환하여 데이터를 전달
    }

    @GetMapping("/webtoon")
    public String webtoon(Model model) {
        //'webtoon' 태그를 가진 키워드를 내림차순으로 정렬해서 가져옴
        List<TestDTO> keywords = testService.findAllByTagOrderByDesc("webtoon");

        // NewsApi를 통해 기사 정보를 가져옴
        for (TestDTO keyword : keywords) {
            try {
                // NewsApi의 search 메서드를 통해 데이터를 가져옴
                ResponseEntity<Map<String, Object>> responseEntity = newsApiController.search(keyword.getKeyword(), "1", "1", "sim");

                // API 응답에서 데이터 파싱
                String jsonResponse = (String) responseEntity.getBody().get("naverData");
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(jsonResponse);
                JsonNode itemsNode = rootNode.path("items");

                if (itemsNode.isArray() && itemsNode.size() > 0) {
                    JsonNode firstArticle = itemsNode.get(0);
                    String title = firstArticle.path("title").asText().replaceAll("<.*?>", "").replaceAll("&quot;", "");
                    String link = firstArticle.path("link").asText().replace("\\", "");

                    keyword.setArticleUrl(link);
                    keyword.setArticleTitle(title);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 위에서 저장한 키워드를 model객체에 담아 webtoon페이지로 전송
        model.addAttribute("keywords", keywords);
        return "webtoon";
    }

    @GetMapping("/actor")
    public String actor(Model model) {
        //'actor' 태그를 가진 키워드를 내림차순으로 정렬해서 가져옴
        List<TestDTO> keywords = testService.findAllByTagOrderByDesc("actor");

        // NewsApi를 통해 기사 정보를 가져옴
        for (TestDTO keyword : keywords) {
            try {
                // NewsApi의 search 메서드를 통해 데이터를 가져옴
                ResponseEntity<Map<String, Object>> responseEntity = newsApiController.search(keyword.getKeyword(), "1", "1", "sim");

                // API 응답에서 데이터 파싱
                String jsonResponse = (String) responseEntity.getBody().get("naverData");
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(jsonResponse);
                JsonNode itemsNode = rootNode.path("items");

                if (itemsNode.isArray() && itemsNode.size() > 0) {
                    JsonNode firstArticle = itemsNode.get(0);
                    String title = firstArticle.path("title").asText().replaceAll("<.*?>", "").replaceAll("&quot;", "");
                    String link = firstArticle.path("link").asText().replace("\\", "");

                    keyword.setArticleUrl(link);
                    keyword.setArticleTitle(title);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 키워드 리스트를 모델에 추가
        model.addAttribute("keywords", keywords); // 여기에 키워드를 추가해야 함

        return "actor"; // Thymeleaf HTML 템플릿을 반환
    }

    @GetMapping("/wordCloud")
    public String wordCloudPage(Model model) {
        // 키워드의 총 검색량 순위를 내림차순으로 가져옴
        List<TestDTO> keywords = testService.findTop50ByTotalOrderByDesc();
        System.out.println("Keywords: " + keywords); // 데이터 확인

        // 모델에 키워드 데이터를 저장
        model.addAttribute("keywordCloudData", keywords);
        return "wordCloud"; // wordCloud.html로 데이터를 보냄
    }

//    @GetMapping("/wordCloud2")
//    public String wordCloud2Page(Model model) {
//        // 키워드의 총 검색량 순위를 내림차순으로 가져옴
//        List<TestDTO> keywords = testService.findTop50ByTotalOrderByDesc();
//        System.out.println("Keywords: " + keywords); // 데이터 확인
//
//        // 모델에 키워드 데이터를 저장
//        model.addAttribute("keywordCloudData", keywords);
//        return "wordCloud2"; // wordCloud.html로 데이터를 보냄
//    }

//    @GetMapping("/wordCloud2")
//    public String wordCloud2Page(Model model) throws Exception {
//        // 키워드 데이터를 가져와서 JSON으로 변환
//        List<TestDTO> keywords = testService.findTop50ByTotalOrderByDesc();
//        ObjectMapper objectMapper = new ObjectMapper();
//        String keywordJson = objectMapper.writeValueAsString(keywords);
//
//        // 모델에 키워드 데이터를 저장 (JSON 형식)
//        model.addAttribute("keywordCloudData", keywordJson);
//        return "wordCloud2"; // wordCloud2.html로 전달
//    }

    @PostMapping("/searchKeyword")
    public String index(@RequestParam("keyword_search") String keyword_search, Model model) {
        model.addAttribute("keyword_search", keyword_search);
        return "searchKeyword";
    }
}
