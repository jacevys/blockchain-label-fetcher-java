package com.jacevys.intelligenceapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api") // 可選：定義通用的 API 前綴
public class HelloController {

    @GetMapping("/hello") // 定義一個 /api/hello 的 GET 路徑
    public String sayHello() {
        return "Hello, World!";
    }
}