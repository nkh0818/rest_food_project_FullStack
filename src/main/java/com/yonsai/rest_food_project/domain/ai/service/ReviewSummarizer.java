package com.yonsai.rest_food_project.domain.ai.service;

import com.yonsai.rest_food_project.domain.ai.entity.ReviewResult;

import dev.langchain4j.service.UserMessage;

// 문자를 조각낸다

public interface ReviewSummarizer {

    @UserMessage("""

        너는 휴게소 리뷰 분석가야. 아래 규칙에 맞춰 JSON으로 반환해 줘.
        -점수(score)는 반드시 다음 중 하나여야 해: POSITIVE, NEGATIVE, NEUTRAL
        -한줄요약(summary): 리뷰 전체를 요약한 30자 이내의 한 문장.
        -태그(tags): 리뷰에서 알 수 있는 휴게소에 대한 명사형을 1~3개 사이로 추출 (예:["돈까스맛집", "주차장넓음", "청결함"])

        리뷰내용 : {{it}}
    """)
    ReviewResult analyze(String reviews);
}