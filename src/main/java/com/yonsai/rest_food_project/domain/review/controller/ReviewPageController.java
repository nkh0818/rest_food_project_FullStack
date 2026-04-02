package com.yonsai.rest_food_project.domain.review.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReviewPageController {

    @GetMapping("/review")
    public String reviewPage() {
        return "review/review_page";
    }

    @GetMapping("/review/edit")
    public String reviewEditPage() {
        return "review/review_edit";
    }
}