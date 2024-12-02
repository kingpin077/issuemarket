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
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    public String mainPage(
            @RequestParam(defaultValue = "0") int totalPage,  // 이름 변경
            @RequestParam(defaultValue = "0") int pcPage,     // PC용 페이지 파라미터 추가
            Model model) {

        // 각각의 페이지네이션을 위한 데이터 조회
        Page<TestDTO> totalRankings = testService.findAllByTotalOrderByDescPaged(totalPage);
        Page<TestDTO> pcRankings = testService.findAllByPcOrderByDescPaged(pcPage);
        // 상위 50개의 키워드를 총 검색량 기준 내림차순으로 가져옴 (wordCloud 용 데이터)
        List<TestDTO> topKeywords = testService.findTop100ByTotalOrderByDesc();

        // 모델에 데이터 추가 - 페이지 번호를 구분해서 전달
        model.addAttribute("keyword", totalRankings);
        model.addAttribute("keyword2", pcRankings);
        model.addAttribute("currentTotalPage", totalPage);    // Total 페이지 번호
        model.addAttribute("currentPcPage", pcPage);         // PC 페이지 번호
        model.addAttribute("totalPages", totalRankings.getTotalPages());
        model.addAttribute("pcTotalPages", pcRankings.getTotalPages());
        model.addAttribute("keywordCloudData", topKeywords); // wordCloud.html 용

        return "index";
    }

    @GetMapping(value = {"/index2"})
    public String mainPage2(
            @RequestParam(defaultValue = "0") int totalPage,  // 이름 변경
            @RequestParam(defaultValue = "0") int pcPage,     // PC용 페이지 파라미터 추가
            Model model) {

        // 각각의 페이지네이션을 위한 데이터 조회
        Page<TestDTO> totalRankings = testService.findAllByTotalOrderByDescPaged(totalPage);
        Page<TestDTO> pcRankings = testService.findAllByPcOrderByDescPaged(pcPage);
        // 상위 50개의 키워드를 총 검색량 기준 내림차순으로 가져옴 (wordCloud 용 데이터)
        List<TestDTO> topKeywords = testService.findTop100ByTotalOrderByDesc();

        // 모델에 데이터 추가 - 페이지 번호를 구분해서 전달
        model.addAttribute("keyword", totalRankings);
        model.addAttribute("keyword2", pcRankings);
        model.addAttribute("currentTotalPage", totalPage);    // Total 페이지 번호
        model.addAttribute("currentPcPage", pcPage);         // PC 페이지 번호
        model.addAttribute("totalPages", totalRankings.getTotalPages());
        model.addAttribute("pcTotalPages", pcRankings.getTotalPages());
        model.addAttribute("keywordCloudData", topKeywords); // wordCloud.html 용

        return "index2";
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

    @GetMapping("/movie")
    public String movie(@RequestParam(defaultValue = "0") int page, Model model) {
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
        return "movie";
    }

    @GetMapping("/music")
    public String music(@RequestParam(defaultValue = "0") int page, Model model) {
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
        return "music";
    }
    @GetMapping("/wordCloud")
    public String wordCloudPage(Model model) {
        // 키워드의 총 검색량 순위를 내림차순으로 가져옴
        List<TestDTO> keywords = testService.findTop100ByTotalOrderByDesc();
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

        //키워드 명 가져오기
        String keywordSearch = (String) data.get("keyword_search");

        // 현재 날짜 가져오기
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HHmm"));

        // 파일 이름 생성
        String fileName = String.format("%s_%s_%s.xlsx", keywordSearch, currentDate, currentTime);

        // 파일 이름 UTF-8 인코딩
        String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");

        // Excel 데이터 생성
        byte[] excelFileData = testService.generateExcelFile(data);
        System.out.println("Generated file name: " + fileName);
        System.out.println("Excel data size: " + excelFileData.length);

        // 파일 다운로드 응답 생성
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelFileData);


    }


}
