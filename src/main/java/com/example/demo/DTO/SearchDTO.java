package com.example.demo.DTO;

public class SearchDTO {
    private String keyword_search;

    public SearchDTO(String keyword_search) {
        this.keyword_search = keyword_search;
    }

    @Override
    public String toString() {
        return keyword_search;
    }
}
