package com.yonsai.rest_food_project.domain.restArea.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yonsai.rest_food_project.domain.restArea.entity.YoutubePlaylist;

// YoutubeRepository.java
public interface YoutubeRepository extends JpaRepository<YoutubePlaylist, Long> {
    List<YoutubePlaylist> findBySearchKey(String searchKey);

    boolean existsByPlaylistId(String playlistId); // 중복 체크용
}