package com.example.demo;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public String homepage(){
        return "login";
    }

    @GetMapping("/success")
    public String showSuccessPage() {
        return "success";
    }

    @GetMapping("/logout")
    public String showHomePage() {
        return "login";
    }

    @PostMapping("/login_process")
    public String login(@RequestParam("userId") String userId, @RequestParam("userPwd") String userPwd, Model model) {
        Optional<User> optUser = userRepository.findByUserId(userId);
        if (optUser.isPresent() && optUser.get().getUserPwd().equals(userPwd)) {
            System.out.println("Database userPwd: " + optUser.get().getUserPwd());
            return "success";
        }
        model.addAttribute("error", "로그인 실패");
        return "login";
    }

    @GetMapping("/main")
    public String mainPage() {
        return "main";
    }
}
