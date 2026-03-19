package com.yonsai.rest_food_project.domain.restArea.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.yonsai.rest_food_project.domain.restArea.entity.YoutubePlaylist;
import com.yonsai.rest_food_project.domain.restArea.repository.YoutubeRepository;
import com.yonsai.rest_food_project.domain.restArea.service.YoutubeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/music")
@RequiredArgsConstructor
public class MusicController {

    private final YoutubeService youtubeService;
    private final YoutubeRepository youtubeRepository;

    /**
     * 1. 기본 화면 (내 DB에 있는 것만 보여주기)
     * URL: /music/list
     */
    @GetMapping("/list")
    public String showMyList(Model model) {
        // API 호출 없이 순수하게 DB에 저장된 모든 플리를 가져옵니다.
        List<YoutubePlaylist> myPlaylists = youtubeRepository.findAll();

        model.addAttribute("playlists", myPlaylists);
        return "playlist_view"; // 저장된 목록 전용 HTML
    }

    /**
     * 2. 검색어 추가 기능 (버튼 누르면 DB에 저장)
     * URL: /music/search?q=검색어
     */
    @GetMapping("/search")
    public String searchAndSave(@RequestParam String q, Model model) {
        // 서비스 로직에 의해 DB에 없으면 API 호출 후 저장, 있으면 DB 반환
        List<YoutubePlaylist> searchResults = youtubeService.getMusicPlaylists(q);

        model.addAttribute("playlists", searchResults);
        model.addAttribute("searchKeyword", q);

        // 저장이 완료된 후 목록 화면으로 리다이렉트하거나 결과를 보여줍니다.
        return "redirect:/music/list";
    }
}