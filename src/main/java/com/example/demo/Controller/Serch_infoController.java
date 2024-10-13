package com.example.demo.Controller;

import com.example.demo.Entity.Serch_infoEntity;
import com.example.demo.Entity.UserEntity;
import com.example.demo.Repository.Serch_infoRepository;
import com.example.demo.Repository.UserRepository; // UserRepository 추가
import com.example.demo.Service.Serch_infoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/keywords")
public class Serch_infoController {

    @Autowired
    private Serch_infoService filterService;

    @Autowired
    private Serch_infoRepository filterRepository;

    @Autowired
    private UserRepository userRepository; // UserRepository 주입

    @PostMapping
    public ResponseEntity<?> saveKeyword(@RequestBody Map<String, String> request) {
        String keyword = request.get("keyword");

        // 웹툰 페이지에서 키워드를 클릭한 경우 해당 키워드와 오늘 날짜를 filter 테이블에 저장
        if (keyword != null && !keyword.isEmpty()) {
            // 로그인한 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Object principal = authentication.getPrincipal();

            String userId = null;
            boolean gender = false;

            // principal이 UserDetails인지 또는 String인지 확인
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                userId = userDetails.getUsername(); // 로그인한 사용자의 아이디

                // UserRepository에서 UserEntity 가져오기
                Optional<UserEntity> userEntity = userRepository.findByUserId(userId);
                if (userEntity.isPresent()) {
                    gender = userEntity.get().isGender(); // 사용자의 성별
                }
            } else if (principal instanceof String) {
                userId = (String) principal;

                // UserRepository에서 UserEntity 가져오기
                Optional<UserEntity> userEntity = userRepository.findByUserId(userId);
                if (userEntity.isPresent()) {
                    gender = userEntity.get().isGender(); // 사용자의 성별
                }
            }

            // Serch_infoEntity 생성 및 저장
            Serch_infoEntity filterEntity = new Serch_infoEntity();
            filterEntity.setKeyword(keyword);  // 클릭한 키워드 설정
            filterEntity.setDate(LocalDateTime.now());  // 오늘 날짜 설정
            filterEntity.setUserId(userId); // 사용자 아이디 설정
            filterEntity.setGender(gender); // 성별 설정 (DB에서 1:남성 / 0:여성)
            filterRepository.save(filterEntity);

            return ResponseEntity.ok("Keyword saved successfully");
        } else {
            return ResponseEntity.badRequest().body("Keyword is missing or empty");
        }
    }
}
