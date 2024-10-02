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
        List<TestDTO> keywords = testService.findAllByTotalOrderByDesc();
        System.out.println("Keywords: " + keywords); // 데이터 확인
        List<TestDTO> keywords2 = testService.findAllByPcOrderByDesc();
        System.out.println("Keywords2: " + keywords2); // 데이터 확인
        model.addAttribute("keyword", testService.findAllByTotalOrderByDesc());
        model2.addAttribute("keyword2", testService.findAllByPcOrderByDesc());
        return "index";
    }

    @GetMapping("/webtoon")
    public String webtoon(Model model) {
        List<TestDTO> keywords = testService.findAllByTagOrderByDesc("webtoon");
        model.addAttribute("keyword", keywords);
        return "webtoon";
    }

    @GetMapping("/actor")
    public String actor(Model model) {
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
                    String title = firstArticle.path("title").asText().replaceAll("<.*?>", "");
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



    @PostMapping("/searchKeyword")
    public String index(@RequestParam("keyword_search") String keyword_search, Model model) {
        model.addAttribute("keyword_search", keyword_search);
        //System.out.println(keyword);
        return "searchKeyword";
    }
}
