package com.yonsai.rest_food_project.domain.favorite.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yonsai.rest_food_project.domain.favorite.Service.FavoriteService;
import com.yonsai.rest_food_project.domain.restArea.dto.RestAreaResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    // 현재 로그인한 사용자의 정보와 휴게소 정보
    @PostMapping("/{stdRestCd}")
    public ResponseEntity<?> toggle(
            @PathVariable String stdRestCd,
            Principal principal) {

        if (principal == null) {
            log.warn("로그인되지 않은 사용자");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        Boolean result = favoriteService.toggleFavorite(principal.getName(), stdRestCd);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyList(Principal principal) {
        if (principal == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        List<RestAreaResponseDto> favorites = favoriteService.findAllByUserEmailWithRestArea(principal.getName());
        return ResponseEntity.ok(favorites);
    }
}
