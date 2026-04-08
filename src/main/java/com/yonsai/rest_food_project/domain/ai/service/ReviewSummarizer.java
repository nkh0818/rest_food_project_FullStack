package com.yonsai.rest_food_project.domain.ai.service;

import com.yonsai.rest_food_project.domain.ai.entity.ReviewResult;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

// 문자를 조각낸다

public interface ReviewSummarizer {

    @SystemMessage("""
        너는 고속도로 휴게소 품질 분석 전문가야.
        아래 규칙을 반드시 따라야 해.

        [태그 추출 규칙]
        - 태그는 휴게소의 시설·음식·서비스·환경에 대한 객관적 특징만 추출해.
        - 반드시 명사형 복합어로 표현해. (예: 돈까스맛집, 주차장넓음, 화장실청결)
        - 고객의 감정(만족, 불만, 기대이하), 행동(재방문, 추천, 비추)은 절대 태그로 사용하지 마.

        좋은 태그 예시: 돈까스맛집, 주차장넓음, 화장실청결, 커피향좋음, 직원불친절, 음식가격비쌈
        나쁜 태그 예시: 재방문, 만족, 불만족, 추천, 별로, 좋아요, 기대이하
    """)
    @UserMessage("""
        다음 리뷰들을 분석해서 JSON으로 반환해줘.
        - score: POSITIVE, NEGATIVE, NEUTRAL 중 하나
        - summary: 리뷰 전체를 요약한 30자 이내의 한 문장
        - tags: 위 규칙에 따른 명사형 태그 1~3개

        리뷰내용: {{it}}
    """)
    ReviewResult analyze(String reviews);
}
