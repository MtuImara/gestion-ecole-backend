package com.gescom.ecole.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "forward:/login.html";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login.html";
    }
    
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard.html";
    }
}
