package com.example.demo.Controller;

import com.example.demo.DTO.UserDTO;
import com.example.demo.TestDTO;
import com.example.demo.TestService;
import com.example.demo.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class UserController {
    @Autowired
    private TestService testService;
    private final UserService userService;

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

    @GetMapping("/main")
    public String mainPage(Model model, Model model2) {
        List<TestDTO> keywords = testService.findAllByTotalOrderByDesc();
        System.out.println("Keywords: " + keywords); // 데이터 확인
        List<TestDTO> keywords2 = testService.findAllByPcOrderByDesc();
        System.out.println("Keywords2: " + keywords2); // 데이터 확인
        model.addAttribute("keyword", testService.findAllByTotalOrderByDesc());
        model2.addAttribute("keyword2", testService.findAllByPcOrderByDesc());
        return "main";
    }

    @GetMapping("/webtoon")
    public String webtoon(Model model) {
        List<TestDTO> keywords = testService.findAllByTagOrderByDesc("webtoon");

        model.addAttribute("keyword", keywords);
        return "webtoon";
    }

    @GetMapping("/actor")
    public String actor(Model model) {
        List<TestDTO> keywords = testService.findAllByTagOrderByDesc("actor");

        model.addAttribute("keyword", keywords);
        return "actor";
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }

//    @PostMapping("/search")
//    public String search(@RequestParam("keyword") String keyword, Model model) {
//        // 여기에 검색어를 이용한 작업을 수행합니다.
//        // 예를 들어, 검색어를 서비스나 API에 전달하고 결과를 받아와서 모델에 추가합니다.
//        // 모델에 추가된 결과는 index 페이지에서 표시됩니다.
//        String result = getSearchResult(keyword); // 검색 결과를 얻는 메소드 호출
//        model.addAttribute("searchResult", result); // 검색 결과를 모델에 추가
//        return "index"; // 결과를 표시할 index 페이지로 이동합니다.
//    }
//
//    // 검색 결과를 가져오는 메소드
//    private String getSearchResult(String keyword) {
//        // 여기에 검색어를 이용한 작업을 수행하여 결과를 얻어옵니다.
//        // 예를 들어, 외부 API 호출, 데이터베이스 조회 등을 수행합니다.
//        return "검색어: " + keyword + "에 대한 검색 결과입니다."; // 간단한 예시로 문자열을 반환합니다.
//    }
}
