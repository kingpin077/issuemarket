package com.example.demo.Controller;

import com.example.demo.Entity.TestEntity;
import com.example.demo.Repository.TcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TcController {

    @Autowired
    private TcRepository tcRepository;

    // 차트 데이터를 제공하는 API
    @GetMapping("/chart-data")
    public List<TestEntity> getChartData() {
        return tcRepository.findAll();
    }
}
