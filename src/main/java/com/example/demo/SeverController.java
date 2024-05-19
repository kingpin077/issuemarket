package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;

@Deprecated
public class SeverController {
    @GetMapping("/ad")
    public String ad() {


        return "ad";
    }
}
