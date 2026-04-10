package com.yonsai.rest_food_project.domain.user.history.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class HistoryResponseDTO {

    private Long id;              // 로그 식별자
    private String date;          // 방문/리뷰 날짜 ("2024.03.22")
    private String routeName;      // 고속도로 노선명 ("경부고속도로")
    private String title;          // 여정 제목 ("부산 방향 미식 투어")
    private String aiSummary;      // AI 요약 총평
    private List<StopInfo> stops;  // 해당 여정에서 방문한 휴게소들 리스트

    // 방문한 휴게소의 요약 정보를 담는 내부 DTO
    @Getter
    @Builder
    public static class StopInfo {
        private String stdRestCd;  // 휴게소 코드
        private String name;       // 휴게소 이름
        private String tags;        // 대표 태그 (필터링된 AI 태그 중 하나)
        private String score;      // AI 점수 (aiScore)
        private String theme;
    }
}