package com.yonsai.rest_food_project.domain.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yonsai.rest_food_project.domain.user.dto.UserResponseDTO;
import com.yonsai.rest_food_project.global.auth.PrincipalDetails;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    @GetMapping("/my")
    public ResponseEntity<UserResponseDTO> getMyInfo(
        @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return ResponseEntity.ok(UserResponseDTO.builder()
                .email(principalDetails.getUsername())
                .nickname(principalDetails.getNickname())
                .build());
    }
    
    
}
