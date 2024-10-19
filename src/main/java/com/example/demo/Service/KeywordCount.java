package com.example.demo.Service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeywordCount {
    private String keyword;
    private Long total;

    public KeywordCount(String keyword, Long count) {
        this.keyword = keyword;
        this.total = count;
    }
    @Override
    public String toString() {
        return "KeywordCount{" +
                "keyword='" + keyword + '\'' +
                ", count=" + total +
                '}';
    }


}
