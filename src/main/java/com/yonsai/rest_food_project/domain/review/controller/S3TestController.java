package com.yonsai.rest_food_project.domain.review.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.yonsai.rest_food_project.domain.review.service.S3Service;

import java.io.IOException;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class S3TestController {

    private final S3Service s3Service;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadOnlyImage(@RequestParam("image") MultipartFile image) throws IOException {
        // 이미지만 S3에 올리고 그 URL 주소를 결과로 받음
        String imageUrl = s3Service.uploadFile(image);
        return ResponseEntity.ok(imageUrl);
    }
}