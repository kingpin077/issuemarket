package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestService {
    private final TestRepository testRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(TestService.class);

    @Autowired
    public TestService(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    public List<TestDTO> findAllByPcOrderByDesc() {
        List<TestEntity> entities = testRepository.findTop10ByOrderByPcDesc();

        return entities.stream().map(entity -> {
            TestDTO dto = new TestDTO();
            dto.setKeyword(entity.getKeyword());
            dto.setPc(entity.getPc());
            return dto;
        }).collect(Collectors.toList());
    }


    public List<TestDTO> findAllByTotalOrderByDesc() {
        List<TestEntity> entities = testRepository.findTop10ByOrderByTotalDesc();

        return entities.stream().map(entity -> {
            TestDTO dto = new TestDTO();
            dto.setKeyword(entity.getKeyword());
            dto.setTotal(entity.getTotal());
            return dto;
        }).collect(Collectors.toList());
    }

    public List<List<TestDTO>> findAllByTagOrderByDesc(String tag) {
        List<TestEntity> entities = testRepository.findTop10ByTagOrderByTotalDesc(tag);
        logTop10Results(entities, "Tag: " + tag);

        // 상위 5개와 하위 5개 추출
        List<TestDTO> top5 = entities.subList(0, Math.min(entities.size(), 5)).stream()
                .map(entity -> {
                    TestDTO dto = new TestDTO();
                    dto.setKeyword(entity.getKeyword());
                    dto.setTotal(entity.getTotal());
                    return dto;
                })
                .collect(Collectors.toList());

        List<TestDTO> next5 = entities.subList(Math.max(entities.size() - 5, 0), entities.size()).stream()
                .map(entity -> {
                    TestDTO dto = new TestDTO();
                    dto.setKeyword(entity.getKeyword());
                    dto.setTotal(entity.getTotal());
                    return dto;
                })
                .collect(Collectors.toList());

        return Arrays.asList(top5, next5);
    }

    private void logTop10Results(List<TestEntity> entities, String sortBy) {
        LOGGER.info("Top 10 results sorted by {}: ", sortBy);
        for (int i = 0; i < Math.min(entities.size(), 10); i++) {
            TestEntity entity = entities.get(i);
            LOGGER.info("Keyword: {}, Value: {}", entity.getKeyword(), sortBy.equals("Pc") ? entity.getPc() : entity.getTotal());
        }
    }



}