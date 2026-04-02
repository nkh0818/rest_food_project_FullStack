package com.yonsai.rest_food_project.domain.inquiry.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import com.yonsai.rest_food_project.domain.inquiry.dto.InquiryRequestDTO;
import com.yonsai.rest_food_project.domain.inquiry.dto.InquiryResponseDTO;
import com.yonsai.rest_food_project.domain.inquiry.service.InquiryService;

@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InquiryController {

    private final InquiryService inquiryService;

    @PostMapping
    public ResponseEntity<Void> create(
            @RequestBody InquiryRequestDTO dto,
            @RequestHeader(value = "X-USER-ID", required = false) Long userId) {

        if (userId == null) userId = 1L;

        inquiryService.create(dto.getContent(), userId);

        return ResponseEntity.ok().build();
    }
    
    // 문의목록에 닉네임 띄우기
    @GetMapping
    public ResponseEntity<List<InquiryResponseDTO>> getAll() {
        return ResponseEntity.ok(inquiryService.getAll());
    }
}