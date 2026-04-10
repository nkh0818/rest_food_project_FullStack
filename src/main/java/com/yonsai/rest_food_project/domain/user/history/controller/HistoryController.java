package com.yonsai.rest_food_project.domain.user.history.controller;

import org.springframework.web.bind.annotation.RestController;

import com.yonsai.rest_food_project.domain.user.history.dto.HistoryResponseDTO;
import com.yonsai.rest_food_project.domain.user.history.service.HistoryService;
import com.yonsai.rest_food_project.global.auth.PrincipalDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@Slf4j
@RequestMapping("/api/user/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping
    public ResponseEntity<List<HistoryResponseDTO>> getHistory(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        log.info("Principal: {}", principalDetails);
        return ResponseEntity.ok(historyService.getUserHistory(principalDetails.getUser()));
    }
    

}
