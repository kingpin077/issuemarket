/** 사용자의 인증 관련 조작에 대한 Controller **/

package com.example.demo.Controller;

import com.example.demo.DTO.UserDTO;
import com.example.demo.Service.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@Controller
public class UserAuthController {
    private final UserAuthService userAuthService;

    @GetMapping("/login")
    public String login_page(){
        return "login";
    }

    @GetMapping("/logout")
    public String showHomePage() {
        return "redirect:/login?logout";
    }

    @PostMapping("/signup")
    public String join(@ModelAttribute UserDTO userDTO, Model model) {
        try {
            userAuthService.createUser(userDTO);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "이미 존재하는 아이디 입니다.");
            return "login";
        }

        return "login";
    }

}
