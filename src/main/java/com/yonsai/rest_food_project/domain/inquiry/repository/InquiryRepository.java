package com.yonsai.rest_food_project.domain.inquiry.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.yonsai.rest_food_project.domain.inquiry.entity.Inquiry;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
}