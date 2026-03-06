package com.yonsai.rest_food_project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RestFoodController {

    @GetMapping("/asd")
    public String asd() {
        System.out.println("메인 페이지");
        return "Food_Detail";
    }
}
