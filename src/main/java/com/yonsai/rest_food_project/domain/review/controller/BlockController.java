package com.yonsai.rest_food_project.domain.review.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yonsai.rest_food_project.domain.review.dto.BlockRequestDTO;
import com.yonsai.rest_food_project.domain.review.dto.BlockResponseDTO;
import com.yonsai.rest_food_project.domain.review.service.BlockService;
import com.yonsai.rest_food_project.domain.user.entity.User;
import com.yonsai.rest_food_project.global.auth.PrincipalDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/blocks")
@RequiredArgsConstructor
public class BlockController {

    private final BlockService blockService;

    @GetMapping
    public ResponseEntity<List<BlockResponseDTO>> getBlockedUsers(
        @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        List<BlockResponseDTO> list = blockService.getBlockedUserList(principalDetails.getUser().getId());
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<?> blockUser(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody BlockRequestDTO dto) {

        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요한 서비스입니다.");
        }

        User me = principalDetails.getUser();
        blockService.blockUser(me, dto.getBlockedUserId());

        return ResponseEntity.ok().build();
    }

    //차단 해제
    @DeleteMapping("/{blockedUserId}")
    public ResponseEntity<Void> unblockUser(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long blockedUserId) {
        blockService.unblockUser(principalDetails.getUser(), blockedUserId);
        return ResponseEntity.ok().build();
    }


}
