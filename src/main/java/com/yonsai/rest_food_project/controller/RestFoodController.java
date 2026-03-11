package com.yonsai.rest_food_project.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.yonsai.rest_food_project.entity.Food;
import com.yonsai.rest_food_project.entity.RestArea;
import com.yonsai.rest_food_project.repository.FoodRepository;
import com.yonsai.rest_food_project.repository.RestAreaRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/food") // 모든 경로는 /food로 시작 (예: /food/list, /food/1)
public class RestFoodController {

    private final RestAreaRepository restAreaRepository;
    private final FoodRepository foodRepository;

    @GetMapping("/list")
    public String list(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "start", required = false) String start,
            @RequestParam(value = "end", required = false) String end,
            Model model) {

        List<RestArea> allList;
        String finalSearch = (search != null) ? search.trim() : "";

        // 1. 경로 기반 자동 노선 설정
        if (start != null && end != null && !start.isEmpty() && !end.isEmpty()) {
            if (start.contains("서울") && end.contains("부산"))
                finalSearch = "경부";
            else if (start.contains("서울") && end.contains("강릉"))
                finalSearch = "영동";
            else if (start.contains("서울") && end.contains("목포"))
                finalSearch = "서해안";
        }

        // 2. 검색어가 없으면 전체 다 가져오기 (무조건 나오게!)
        if (!finalSearch.isEmpty()) {
            allList = restAreaRepository.findByNameContainingOrRouteNameContaining(finalSearch, finalSearch);
        } else {
            allList = restAreaRepository.findAll();
        }

        // [중요!] HTML에서 th:each="area : ${restAreas}" 라고 썼기 때문에 이름을 맞춰야 합니다.

        // 맛집 휴게소 리스트 생성
        List<RestArea> restAreas = allList.stream()
                .filter(area -> !area.getName().contains("주유소") &&
                        !area.getName().contains("충전소") &&
                        !area.getName().contains("쉼터"))
                .toList();

        // 최저가 주유소 리스트 생성
        List<RestArea> gasStations = allList.stream()
                .filter(area -> area.getName().contains("주유소") ||
                        area.getName().contains("충전소") ||
                        (area.getGasolinePrice() != null && area.getGasolinePrice() > 0))
                .toList();

        // 3. 모델에 'restAreas'와 'gasStations'라는 이름으로 담아서 보냄
        model.addAttribute("restAreas", restAreas);
        model.addAttribute("gasStations", gasStations);
        model.addAttribute("search", finalSearch);

        return "food/list";
    }

    // 2. 특정 휴게소 클릭 시 상세 메뉴판 보여주기
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        RestArea area = restAreaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 휴게소가 없습니다."));

        // 해당 휴게소에 소속된 모든 음식 리스트 가져오기
        List<Food> foods = foodRepository.findByRestAreaId(id);

        model.addAttribute("area", area);
        model.addAttribute("foods", foods);
        return "food/detail"; // templates/food/detail.html
    }
}
