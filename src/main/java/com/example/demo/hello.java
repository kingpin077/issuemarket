package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class hello {
    String a;
    KeywordService ks = new KeywordService();

    @GetMapping ("/hc")
    public String hello() {

        return ks.getKeywordData("범죄도시");
    }

    @GetMapping ("/keyword_submit")
    public String keywordSubmit() {
        return ks.getKeywordData("범죄도시");
    }
}
