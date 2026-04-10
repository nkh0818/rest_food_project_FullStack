package com.yonsai.rest_food_project.domain.review.service;

import com.yonsai.rest_food_project.domain.review.repository.ReviewRepository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

import com.yonsai.rest_food_project.domain.review.dto.BlockResponseDTO;
import com.yonsai.rest_food_project.domain.review.dto.ReviewResponseDTO;
import com.yonsai.rest_food_project.domain.review.entity.Block;
import com.yonsai.rest_food_project.domain.review.entity.Review;
import com.yonsai.rest_food_project.domain.review.repository.BlockRepository;
import com.yonsai.rest_food_project.domain.user.entity.User;
import com.yonsai.rest_food_project.domain.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// [사용자 차단 서비스] 불량 이용자 차단/해제 및 차단된 사용자의 리뷰를 필터링하는 기능을 제공

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockServiceImpl implements BlockService {

    private final ReviewRepository reviewRepository;
    private final BlockRepository blockRepository;
    private final UserRepository userRepository;

    // [유저 차단] 특정 사용자를 차단 리스트에 추가합니다. (자기 자신 차단 불가 및 중복 차단 방지 로직 포함)
    @Override
    @Transactional
    public void blockUser(User me, Long blockedUserId) {

        User blocker = userRepository.findById(me.getId())
                .orElseThrow(() -> new IllegalArgumentException("내 정보를 찾을 수 없습니다."));

        if (blocker.getId().equals(blockedUserId)) {
            throw new IllegalArgumentException("자기 자신은 차단할 수 없습니다.");
        }

        User target = userRepository.findById(blockedUserId)
                .orElseThrow(() -> new IllegalArgumentException("차단 대상 사용자를 찾을 수 없습니다."));

        // 이미 차단된 관계인지 확인하여 중복 저장 방지
        if (blockRepository.existsByBlockerAndBlocked(blocker, target)) {
            return;
        }

        log.info("차단 실행: {} - {}", blocker, target);

        Block block = Block.builder()
                .blocker(blocker)
                .blocked(target)
                .build();

        blockRepository.save(block);
    }

    //[커뮤니티 리뷰 조회] 차단한 유저의 게시물을 제외하고 리뷰 목록을 페이징하여 가져옵니다.

    @Override
    public PagedModel<ReviewResponseDTO> getCommunityReviews(Long userId, Pageable pageable) {
        Page<Review> reviewPage;

        // 비로그인 시 전체 조회, 로그인 시 차단 유저 제외 쿼리 실행
        if (userId != null && userId > 0) {
            reviewPage = reviewRepository.findAllExcludingBlocked(userId, pageable);
        } else {
            reviewPage = reviewRepository.findAll(pageable);
        }

        return new PagedModel<>(reviewPage.map(ReviewResponseDTO::from));
    }

    //[차단 해제] 차단 리스트에서 특정 유저를 삭제하여 다시 리뷰가 보이도록 합니다.
    @Transactional
    @Override
    public void unblockUser(User me, Long blockedUserId) {
        User blocker = userRepository.findById(me.getId())
                .orElseThrow(() -> new IllegalArgumentException("내 정보를 찾을 수 없습니다."));

        // 차단 내역 삭제
        blockRepository.deleteByBlockerAndBlockedId(blocker, blockedUserId);
        log.info("차단 해제 실행: {} -> {}", blocker.getId(), blockedUserId);
    }

    //[차단 목록 조회] 내가 현재 차단 중인 유저들의 리스트(닉네임, 차단 일시 등)를 확인
    @Override
    public List<BlockResponseDTO> getBlockedUserList(Long myId) {

        List<Block> blocks = blockRepository.findAllByBlockerId(myId);

        return blocks.stream()
                .map(block -> BlockResponseDTO.builder()
                        .userId(block.getBlocked().getId())
                        .nickname(block.getBlocked().getNickname())
                        .createdAt(block.getCreatedAt().toString())
                        .build())
                .collect(Collectors.toList());
    }

}
