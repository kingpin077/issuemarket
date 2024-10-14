package com.example.demo.Service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeywordCount {
    private String keyword;
    private Long count;

    public KeywordCount(String keyword, Long count) {
        this.keyword = keyword;
        this.count = count;
    }

}
