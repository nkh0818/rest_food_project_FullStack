package com.yonsai.rest_food_project.global.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NicknameGenerator {

    private final NicknameProperties nicknameProperties;

    public String generate(){

        List<String> adjList = nicknameProperties.getAdjectives() != null 
                           ? nicknameProperties.getAdjectives() 
                           : List.of("신난");
    List<String> nounList = nicknameProperties.getNouns() != null 
                            ? nicknameProperties.getNouns() 
                            : List.of("여행자");

        List<String> adjs = new ArrayList<>(adjList);
    List<String> nouns = new ArrayList<>(nounList);

        Collections.shuffle(adjs);
        Collections.shuffle(nouns);

        String adj = adjs.get(0);
        String noun = nouns.get(0);

        int number = (int)(Math.random() * 900) + 100;

        return String.format("%s_%s_%d", adj, noun, number);
    }
}
