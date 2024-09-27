package com.example.demo;

import com.example.demo.Controller.SeverController2;
import com.example.demo.Service.KeywordService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class hello {
    String a;
    KeywordService ks = new KeywordService();
    SeverController2 sv =  new SeverController2();

    @GetMapping ("/hc")
    public String hello() {

        return String.valueOf(sv.getKeywordRatio("고혼진"));
    }

    @GetMapping ("/keyword_submit")
    public String keywordSubmit() {
        return ks.getKeywordData("범죄도시");
    }
}
