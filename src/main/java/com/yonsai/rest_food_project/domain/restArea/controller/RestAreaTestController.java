package com.yonsai.rest_food_project.domain.restArea.controller;

import com.yonsai.rest_food_project.domain.restArea.entity.RestArea;
import com.yonsai.rest_food_project.domain.restArea.repository.RestAreaRepository; // 사용자님의 리포지토리 경로 확인
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/restareas")
@RequiredArgsConstructor // Repository 주입을 위해 필요합니다.
public class RestAreaTestController {

    private final RestAreaRepository restAreaRepository;

    @GetMapping("/search")
    public List<RestArea> searchFromDb(@RequestParam("start") String start,
            @RequestParam("end") String end) {
        // 🚀 DB에 저장된 모든 휴게소 데이터를 리액트로 던집니다.
        // 나중에 검색 로직이 완성되면 findAll() 대신 검색 메서드를 넣으세요.
        return restAreaRepository.findAll();
    }
}