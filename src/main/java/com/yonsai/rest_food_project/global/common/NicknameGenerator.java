package com.yonsai.rest_food_project.global.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class NicknameGenerator {

    private final NicknameProperties nicknameProperties;

    public String generate() {
    // 1. 리스트 가져오기 및 null 체크
    List<String> adjList = nicknameProperties.getAdjectives();
    List<String> nounList = nicknameProperties.getNouns();

    if (adjList == null || adjList.isEmpty()) adjList = List.of("신난");
    if (nounList == null || nounList.isEmpty()) nounList = List.of("여행자");

    // 2. 랜덤 인덱스로 하나씩 뽑기
    ThreadLocalRandom random = ThreadLocalRandom.current();
    String adj = adjList.get(random.nextInt(adjList.size()));
    String noun = nounList.get(random.nextInt(nounList.size()));
    int number = random.nextInt(100, 1000);

    // 3. 변수에 담기 (이게 핵심!)
    String finalNickname = String.format("%s_%s_%d", adj, noun, number);

    // 4. 로그로 정체 확인하기
    log.info("🎯 생성된 랜덤 닉네임: [{}]", finalNickname);

    return finalNickname;
}
}