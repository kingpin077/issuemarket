package com.example.demo.Controller;

import com.example.demo.Service.KeywordCount;
import com.example.demo.Service.KeywordService;
import com.example.demo.Service.WordcloudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WordcloudController {
    @Autowired
    private WordcloudService wordcloudService;

    @GetMapping("/word")
    public List<KeywordCount> getKeywordCounts() {
        return wordcloudService.getKeywordCounts();
    }
}
