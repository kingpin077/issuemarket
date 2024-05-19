package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {
    private final TestService testService;

    @Autowired
    public TestController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping("/test1")
    public List<TestDTO> getPcTestData() {
        return testService.findAllByPcOrderByDesc();
    }

    @GetMapping("/test2")
    public List<TestDTO> getMobileTestData() {
        return testService.findAllByMobileOrderByDesc();
    }

    @GetMapping("/test3")
    public List<TestDTO> getTotalTestData() {
        return testService.findAllByTotalOrderByDesc();
    }
}