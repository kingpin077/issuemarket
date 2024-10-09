/** 관리자 페이지 조작을 위한 Controller **/

package com.example.demo.Controller;

import com.example.demo.Entity.UserEntity;
import com.example.demo.Service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/admin")
    public String admin_page(){
        return "admin/index.html";
    }

    @GetMapping("/admin/user_list")
    public String admin_user_list(Model model){
        List<UserEntity> userList = adminService.getAllUsers();
        model.addAttribute("userList", userList);
        return "admin/user_list.html";
    }
}
