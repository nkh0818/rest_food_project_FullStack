package com.yonsai.rest_food_project.domain.review.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    String upload(MultipartFile file);
}
