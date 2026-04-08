package com.yonsai.rest_food_project.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadFile(MultipartFile file) throws IOException {
        // 1. 파일 이름 중복 방지를 위해 랜덤 ID 생성
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        // 2. S3에 올릴 요청 객체 생성
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        // 3. 실제 업로드 실행
        s3Client.putObject(putObjectRequest,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        // 4. 업로드된 파일의 URL 주소 반환 (나중에 DB에 저장할 용도)
        return String.format("https://%s.s3.ap-northeast-2.amazonaws.com/%s", bucket, fileName);
    }
}