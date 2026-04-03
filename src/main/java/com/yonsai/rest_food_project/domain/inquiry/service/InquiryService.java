package com.yonsai.rest_food_project.domain.inquiry.service;

import java.util.List;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.yonsai.rest_food_project.domain.inquiry.dto.InquiryResponseDTO;
import com.yonsai.rest_food_project.domain.inquiry.entity.Inquiry;
import com.yonsai.rest_food_project.domain.inquiry.repository.InquiryRepository;
import com.yonsai.rest_food_project.domain.user.entity.User;
import com.yonsai.rest_food_project.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;

    public void create(String content, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        Inquiry inquiry = Inquiry.builder()
                .content(content)
                .user(user)
                .build();

        inquiryRepository.save(inquiry);
    }
    
    // 문의 목록에 닉네임 띄우기
    public List<InquiryResponseDTO> getAll() {

        return inquiryRepository.findAll()
                .stream()
                .map(InquiryResponseDTO::from)
                .toList();
    }
}