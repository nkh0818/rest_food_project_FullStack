package com.yonsai.rest_food_project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class DetailController {

    @GetMapping("/detail/{id}")
    public String detailPage(@PathVariable Long id, Model model) {
        model.addAttribute("restAreaId", id);
        return "detail";
    }
}