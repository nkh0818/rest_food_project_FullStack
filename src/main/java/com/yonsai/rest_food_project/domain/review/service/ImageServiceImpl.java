package com.yonsai.rest_food_project.domain.review.service;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageServiceImpl implements ImageService {

    private final String uploadDir = "C:/upload/";

    @Override
    public String upload(MultipartFile file) {

        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            Path path = Paths.get(uploadDir + fileName);

            Files.write(path, file.getBytes());

            return "http://localhost:8080/images/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패");
        }
    }
}
