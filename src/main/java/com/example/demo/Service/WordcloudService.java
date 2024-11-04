package com.example.demo.Service;

import com.example.demo.Repository.WordcloudRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WordcloudService {
    @Autowired
    private WordcloudRepository wordcloudRepository;

    public List<KeywordCount> getKeywordCounts() {
        List<Object[]> results = wordcloudRepository.findKeywordCount();
        List<KeywordCount> keywordCounts = new ArrayList<>();

        for (Object[] result : results) {
            String keyword = (String) result[0];
            Long count = (Long) result[1];
            keywordCounts.add(new KeywordCount(keyword, count));
        }
        return keywordCounts;
    }

}


