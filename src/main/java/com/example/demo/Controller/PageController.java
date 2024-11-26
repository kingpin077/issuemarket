package com.example.demo.Controller;

import com.example.demo.DTO.TestDTO;
import com.example.demo.Service.TestService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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

    @GetMapping(value = {"/index", "/"})
    public String mainPage(@RequestParam(defaultValue = "0") int page, Model model) {
        // 페이징된 데이터 가져오기
        Page<TestDTO> totalRankings = testService.findAllByTotalOrderByDescPaged(page);
        Page<TestDTO> pcRankings = testService.findAllByPcOrderByDescPaged(page);

        // 전체 키워드 데이터 (총 검색량 순위와 PC 검색량 순위) 가져오기
        List<TestDTO> allTotalRankings = testService.findAllByTotalOrderByDesc();
        List<TestDTO> allPcRankings = testService.findAllByPcOrderByDesc();

        // 상위 50개의 키워드 데이터 가져오기 (워드클라우드 용)
        List<TestDTO> topKeywords = testService.findTop50ByTotalOrderByDesc();

        // 모델에 데이터 추가
        model.addAttribute("keyword", totalRankings);       // 페이징된 총 검색량 데이터
        model.addAttribute("keyword2", pcRankings);         // 페이징된 PC 검색량 데이터
        model.addAttribute("allKeywords", allTotalRankings); // 전체 총 검색량 데이터
        model.addAttribute("allKeywords2", allPcRankings);   // 전체 PC 검색량 데이터
        model.addAttribute("keywordCloudData", topKeywords); // 워드클라우드 데이터
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalRankings.getTotalPages());

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
    public String webtoon(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<TestDTO> webtoons = testService.findAllByTagOrderByDescPaged("webtoon", page);

        // NewsApi 호출 로직
        for (TestDTO webtoon : webtoons.getContent()) {
            try {
                ResponseEntity<Map<String, Object>> responseEntity =
                        newsApiController.search(webtoon.getKeyword(), "1", "1", "sim");

                String jsonResponse = (String) responseEntity.getBody().get("naverData");
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(jsonResponse);
                JsonNode itemsNode = rootNode.path("items");

                if (itemsNode.isArray() && itemsNode.size() > 0) {
                    JsonNode firstArticle = itemsNode.get(0);
                    String title = firstArticle.path("title").asText()
                            .replaceAll("<.*?>", "").replaceAll("&quot;", "");
                    String link = firstArticle.path("link").asText().replace("\\", "");

                    webtoon.setArticleUrl(link);
                    webtoon.setArticleTitle(title);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int totalPages = Math.min(webtoons.getTotalPages(), 7); // 최대 7페이지로 제한

        model.addAttribute("keywords", webtoons.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "webtoon";
    }

    @GetMapping("/actor")
    public String actor(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<TestDTO> actors = testService.findAllByTagOrderByDescPaged("actor", page);

        // NewsApi 호출 로직
        for (TestDTO actor : actors.getContent()) {
            try {
                ResponseEntity<Map<String, Object>> responseEntity =
                        newsApiController.search(actor.getKeyword(), "1", "1", "sim");

                String jsonResponse = (String) responseEntity.getBody().get("naverData");
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(jsonResponse);
                JsonNode itemsNode = rootNode.path("items");

                if (itemsNode.isArray() && itemsNode.size() > 0) {
                    JsonNode firstArticle = itemsNode.get(0);
                    String title = firstArticle.path("title").asText()
                            .replaceAll("<.*?>", "").replaceAll("&quot;", "");
                    String link = firstArticle.path("link").asText().replace("\\", "");

                    actor.setArticleUrl(link);
                    actor.setArticleTitle(title);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int totalPages = Math.min(actors.getTotalPages(), 7); // 최대 7페이지로 제한

        model.addAttribute("keywords", actors.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "actor";
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
    public String searchKeyword(@RequestParam("keyword_search") String keyword_search, Model model) {
        model.addAttribute("keyword_search", keyword_search);
        return "searchKeyword";
    }

    @GetMapping("/searchKeyword")
    public String searchKeywordGet(@RequestParam("keyword_search") String keyword_search, Model model) {
        model.addAttribute("keyword_search", keyword_search);
        return "searchKeyword";
    }

    /** 엑셀 다운로드 **/
    @PostMapping("/exportExcel")
    public ResponseEntity<byte[]> exportExcel(@RequestBody Map<String, Object> data) throws IOException {
        byte[] excelData = testService.generateExcelFile(data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=exported_data.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelData);
    }


}
