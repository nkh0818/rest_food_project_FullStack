package com.yonsai.rest_food_project.domain.inquiry.dto;

import java.time.LocalDateTime;

import com.yonsai.rest_food_project.domain.inquiry.entity.Inquiry;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InquiryResponseDTO {

    private Long id;
    private String content;
    private String nickname;
    private LocalDateTime createdAt;

    public static InquiryResponseDTO from(Inquiry inquiry) {
        return InquiryResponseDTO.builder()
                .id(inquiry.getId())
                .content(inquiry.getContent())
                .nickname(
                    inquiry.getUser() != null 
                        ? inquiry.getUser().getNickname() 
                        : "알 수 없음"
                )
                .createdAt(inquiry.getCreatedAt())
                .build();
    }
}
