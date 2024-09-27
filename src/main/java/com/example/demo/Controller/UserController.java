package com.example.demo.Controller;

import com.example.demo.DTO.UserDTO;
import com.example.demo.NewsApi;
import com.example.demo.DTO.TestDTO;
import com.example.demo.Service.TestService;
import com.example.demo.Service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
public class UserController {
    @Autowired
    private TestService testService;
    private final UserService userService;
    @Autowired
    private NewsApi newsApi;

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
    public String login(@ModelAttribute UserDTO userDTO, Model model) {
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

        // NewsApi를 통해 기사 정보를 가져옴
        for (TestDTO keyword : keywords) {
            try {
                // NewsApi의 search 메서드를 통해 데이터를 가져옴
                ResponseEntity<Map<String, Object>> responseEntity = newsApi.search(keyword.getKeyword(), "1", "1", "sim");

                // API 응답에서 데이터 파싱
                String jsonResponse = (String) responseEntity.getBody().get("naverData");
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(jsonResponse);
                JsonNode itemsNode = rootNode.path("items");

                if (itemsNode.isArray() && itemsNode.size() > 0) {
                    JsonNode firstArticle = itemsNode.get(0);
                    String title = firstArticle.path("title").asText().replaceAll("<.*?>", "");
                    String link = firstArticle.path("link").asText().replace("\\", "");

                    keyword.setArticleUrl(link);
                    keyword.setArticleTitle(title);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        model.addAttribute("keyword", keywords);
        return "actor";
    }



    @PostMapping("/index")
    public String index(@RequestParam("keyword_search") String keyword_search, Model model) {
        model.addAttribute("keyword_search", keyword_search);
        //System.out.println(keyword);
        return "index";
    }

}
