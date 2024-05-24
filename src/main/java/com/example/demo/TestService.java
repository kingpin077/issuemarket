package com.example.demo;

import com.example.demo.TestDTO;
import com.example.demo.TestEntity;
import com.example.demo.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.Arrays;
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


    public List<TestDTO> findAllByMobileOrderByDesc() {
        List<TestEntity> entities = testRepository.findTop10ByOrderByMobileDesc();

        return entities.stream().map(entity -> {
            TestDTO dto = new TestDTO();
            //dto.setNo(entity.getNo());
            dto.setKeyword(entity.getKeyword());
            dto.setMobile(entity.getMobile());
            //dto.setPc(entity.getPc());
            //dto.setTotal(entity.getTotal());
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


}