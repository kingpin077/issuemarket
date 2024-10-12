package com.example.demo.Controller;

import com.example.demo.Entity.UserEntity;
import com.example.demo.Service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdminController {

    private final UserAuthService userAuthService;

    @Autowired
    public AdminController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @GetMapping("/admin")
    public String admin_page(Model model) {
        // 사용자 목록을 가져와서 모델에 추가
        List<UserEntity> userList = userAuthService.getAllUsers(); // 사용자 목록을 가져오는 메서드 호출
        model.addAttribute("users", userList); // 모델에 사용자 목록 추가
        return "admin"; // admin.html을 반환
    }

    // 사용자 목록 페이지
    @GetMapping("/admin/users") // 새로운 메소드 추가
    public String userList(Model model) {
        List<UserEntity> users = userAuthService.getAllUsers(); // 모든 사용자 목록 가져오기
        model.addAttribute("users", users); // 모델에 사용자 목록 추가
        return "user_list"; // user_list.html을 반환
    }
}
