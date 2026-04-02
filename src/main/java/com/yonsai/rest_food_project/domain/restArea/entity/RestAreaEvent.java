package com.yonsai.rest_food_project.domain.restArea.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestAreaEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "TEXT") // 글자 수 제한을 없애고
    private String eventDetail;

    @Column(length = 500)
    private String eventNm; // 이벤트 제목이 길어서 안들어가니 늘림
    private String stdRestCd;
    private String eventSeq;
    private String stime;
    private String etime;
    private String stdRestNm;
    private String routeNm;
    private String svarAddr;
    @Builder.Default // 빌더 사용 시 기본값 적용을 위해 필요 강제 고정시키기
    private int viewCount = 0;
}