/** 관리자 페이지 조작을 위한 Controller **/

package com.example.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/admin")
    public String admin_page(){
        return "admin";
    }
}
