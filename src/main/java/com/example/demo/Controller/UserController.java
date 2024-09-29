package com.example.demo.Controller;

import com.example.demo.DTO.UserDTO;
import com.example.demo.Service.TestService;
import com.example.demo.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@Controller
public class UserController {
    @Autowired
    private TestService testService;
    private final UserService userService;
    @Autowired
    private NewsApiController newsApiController;

    @GetMapping("/login")
    public String login_page(){
        return "login";
    }

    @GetMapping("/success")
    public String showSuccessPage() {
        return "success";
    }

    @GetMapping("/logout")
    public String showHomePage() {
        return "redirect:/login?logout";
    }

    @PostMapping("/signup")
    public String jogin(@ModelAttribute UserDTO userDTO, Model model) {
        try {
            userService.createUser(userDTO);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "이미 존재하는 아이디 입니다.");
            return "login";
        }

        return "login";
    }

}
