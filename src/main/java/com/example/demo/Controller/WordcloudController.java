package com.example.demo.Controller;

import com.example.demo.DTO.TestDTO;
import com.example.demo.Service.KeywordCount;
import com.example.demo.Service.KeywordService;
import com.example.demo.Service.WordcloudService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller
public class WordcloudController {
    @Autowired
    private WordcloudService wordcloudService;

    @GetMapping("/wordCloud2")
    public String wordCloudPage(Model model)  throws JsonProcessingException {
        // 키워드의 총 검색량 순위를 내림차순으로 가져옴
        List<KeywordCount> keywords = wordcloudService.getKeywordCounts();

        ObjectMapper objectMapper = new ObjectMapper();
        String keywordCloudDataJson = objectMapper.writeValueAsString(keywords);

        System.out.println("Keywords: " + keywords); // 데이터 확인

        // 모델에 키워드 데이터를 저장
        model.addAttribute("keywordCloudData", keywords);
        return "wordCloud"; // wordCloud.html로 데이터를 보냄
    }

}

