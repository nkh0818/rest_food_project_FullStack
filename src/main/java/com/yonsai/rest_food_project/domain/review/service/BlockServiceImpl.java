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

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockServiceImpl implements BlockService {

    private final ReviewRepository reviewRepository;
    private final BlockRepository blockRepository;
    private final UserRepository userRepository;

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

    @Override
    public PagedModel<ReviewResponseDTO> getCommunityReviews(Long userId, Pageable pageable) {
        Page<Review> reviewPage;

        // userId가 null이거나 0이면 차단 필터링 없이 전체 조회
        if (userId != null && userId > 0) {
            reviewPage = reviewRepository.findAllExcludingBlocked(userId, pageable);
        } else {
            reviewPage = reviewRepository.findAll(pageable);
        }

        return new PagedModel<>(reviewPage.map(ReviewResponseDTO::from));
    }

    @Transactional
    @Override
    public void unblockUser(User me, Long blockedUserId) {
        User blocker = userRepository.findById(me.getId())
                .orElseThrow(() -> new IllegalArgumentException("내 정보를 찾을 수 없습니다."));

        // 차단 내역 삭제
        blockRepository.deleteByBlockerAndBlockedId(blocker, blockedUserId);
        log.info("차단 해제 실행: {} -> {}", blocker.getId(), blockedUserId);
    }

    @Override
    public List<BlockResponseDTO> getBlockedUserList(Long myId) {
        
        List<Block> blocks = blockRepository.findAllByBlockerId(myId);

        return blocks.stream()
                .map(block -> BlockResponseDTO.builder()
                        .userId(block.getBlocked().getId())
                        .nickname(block.getBlocked().getNickname())
                        .createdAt(block.getCreatedAt().toString()) // 날짜 포맷은 취향대로!
                        .build())
                .collect(Collectors.toList());
    }

}
