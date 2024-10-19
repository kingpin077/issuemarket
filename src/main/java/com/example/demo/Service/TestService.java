package com.example.demo.Service;


import com.example.demo.DTO.TestDTO;
import com.example.demo.Entity.TestEntity;
import com.example.demo.Repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestService {
    private final TestRepository testRepository;
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


    public List<TestDTO> findAllByTagOrderByDesc(String tag) {
        List<TestEntity> entities = testRepository.findTop10ByTagOrderByTotalDesc(tag);

        return entities.stream().map(entity -> {
            TestDTO dto = new TestDTO();
            dto.setKeyword(entity.getKeyword());
            dto.setTotal(entity.getTotal());
            return dto;
        }).collect(Collectors.toList());
    }


    // 총 검색량 상위 30개의 데이터를 가져오는 메서드
    public List<TestDTO> findTop50ByTotalOrderByDesc() {
        List<TestEntity> entities = testRepository.findTop50ByOrderByTotalDesc();

        return entities.stream().map(entity -> {
            TestDTO dto = new TestDTO();
            dto.setKeyword(entity.getKeyword());
            dto.setTotal(entity.getTotal());
            return dto;
        }).collect(Collectors.toList());
    }


}