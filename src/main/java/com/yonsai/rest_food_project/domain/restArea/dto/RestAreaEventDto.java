package com.yonsai.rest_food_project.domain.restArea.dto;

import com.yonsai.rest_food_project.domain.restArea.entity.RestAreaEvent;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RestAreaEventDto {
    private String stdRestCd; // 휴게소/주유소 코드
    private String eventSeq; // 일련번호
    private String stime; // 시작일
    private String etime; // 종료일
    private String eventDetail; // 내용
    private String eventNm; // 제목
    private String stdRestNm; // 휴게소명
    private String routeNm; // 노선명
    private String svarAddr; // 주소

    // Entity로 변환하는 메서드도 업데이트
    // 어짜피 한번 사용임 짧게쓰기 위해 여기에 로직사용
    public RestAreaEvent toEntity() {
        return RestAreaEvent.builder()
                .stdRestCd(this.stdRestCd)
                .eventSeq(this.eventSeq)
                .stime(this.stime)
                .etime(this.etime)
                .eventDetail(this.eventDetail)
                .eventNm(this.eventNm)
                .stdRestNm(this.stdRestNm)
                .routeNm(this.routeNm)
                .svarAddr(this.svarAddr)
                .build();
    }
}