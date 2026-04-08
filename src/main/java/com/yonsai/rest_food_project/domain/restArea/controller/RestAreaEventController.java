package com.yonsai.rest_food_project.domain.restArea.controller;

import com.yonsai.rest_food_project.domain.restArea.entity.RestAreaEvent;
import com.yonsai.rest_food_project.domain.restArea.service.RestAreaEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/events")
@RequiredArgsConstructor
public class RestAreaEventController {

    private final RestAreaEventService eventService;

    @GetMapping("/list")
    public String listEvents(
            @RequestParam("stdRestCd") String stdRestCd,
            @RequestParam("name") String name,
            Model model) {

        // //이벤트 필터링 추가: 해당 휴게소 코드로 검색
        List<RestAreaEvent> events = eventService.getRelatedEvents(stdRestCd, name);

        model.addAttribute("events", events);
        model.addAttribute("restName", name);

        return "event/list"; // templates/event/list.html
    }

    // 전체 목록 조회
    @GetMapping
    public String allEvents(Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        return "event/list";
    }

    // 상세 내용 조회
    @GetMapping("/{id}")
    public String eventDetail(@PathVariable Long id, Model model) {
        model.addAttribute("event", eventService.getEventDetail(id));
        return "event/detail";
    }
}