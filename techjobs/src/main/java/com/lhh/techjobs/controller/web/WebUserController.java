package com.lhh.techjobs.controller.web;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebUserController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
