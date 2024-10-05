package com.example.demo.Controller;

import com.example.demo.Entity.Serch_infoEntity;
import com.example.demo.Repository.Serch_infoRepository;
import com.example.demo.Service.Serch_infoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/keywords")
public class Serch_infoController {

    @Autowired
    private Serch_infoService filterService;
    @Autowired
    private Serch_infoRepository filterRepository;


    @PostMapping
    public ResponseEntity<?> saveKeyword(@RequestBody Map<String, String> request) {
        String keyword = request.get("keyword");
        //webtoon페이지에서 키워드를 클릭한 경우 해당 키워드와 오늘 날짜를 filter테이블에 저장
        if (keyword != null && !keyword.isEmpty()) {
            Serch_infoEntity filterEntity = new Serch_infoEntity();
            filterEntity.setKeyword(keyword);  // 클릭한 키워드 설정
            filterEntity.setDate(LocalDateTime.now());  // 오늘 날짜 설정
            filterRepository.save(filterEntity);
            return ResponseEntity.ok("Keyword saved successfully");
        } else {
            return ResponseEntity.badRequest().body("Keyword is missing or empty");
        }
    }
}
