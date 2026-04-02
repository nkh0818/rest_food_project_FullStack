package com.yonsai.rest_food_project.domain.review.service;

import org.springframework.stereotype.Service;

import com.yonsai.rest_food_project.domain.review.dto.ReviewRequestDTO;
import com.yonsai.rest_food_project.global.common.LocationUtils;
import com.yonsai.rest_food_project.global.exception.RoadQuestException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final LocationUtils locationUtils;

    /** 아래쪽에 적어둔 내용은 throw 예시입니다 변경하시고 사용하셔도 됩니다 */

    @Transactional
    public void createReview(ReviewRequestDTO dto) {
        // 1. 로그 기록 (유지보수용: 누가 어디서 리뷰를 쓰려는지 확인)
        log.info("[리뷰 작성 시작] 휴게소ID: {}, 사용자 위도: {}, 경도: {}", 
                 dto.getRestAreaId(), dto.getUserLat(), dto.getUserLon());

        // 2. 비즈니스 로직: 거리 체크 (예시: 휴게소 위경도는 DB에서 가져왔다고 가정)
        double restAreaLat = 37.123456; 
        double restAreaLon = 127.123456;

        double distance = locationUtils.getDistance(
                dto.getUserLat(), dto.getUserLon(), 
                restAreaLat, restAreaLon
        );

        // 3. 비즈니스 예외 던지기 (핵심!)
        // 여기서 throw를 던지면 GlobalExceptionHandler가 낚아챕니다.
        if (distance > 100) {
            log.warn("[리뷰 작성 거부] 거리 초과: {}m", Math.round(distance));
            throw new RoadQuestException("휴게소와 너무 멉니다. 100m 이내에서만 작성 가능합니다.");
        }

        // 4. 저장 로직 (예외가 안 터졌을 때만 실행됨)
        log.info("[리뷰 저장 성공] 거리: {}m", Math.round(distance));
        // reviewRepository.save(...); 
    }

}
