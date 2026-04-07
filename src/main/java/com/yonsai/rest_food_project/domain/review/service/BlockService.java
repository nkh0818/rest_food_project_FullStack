package com.yonsai.rest_food_project.domain.review.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

import com.yonsai.rest_food_project.domain.review.dto.BlockResponseDTO;
import com.yonsai.rest_food_project.domain.review.dto.ReviewResponseDTO;
import com.yonsai.rest_food_project.domain.user.entity.User;

public interface BlockService {

    void blockUser(User me, Long blockedUserId);

    PagedModel<ReviewResponseDTO> getCommunityReviews(Long userId, Pageable pageable);

    void unblockUser(User me, Long blockedUserId);

    List<BlockResponseDTO> getBlockedUserList(Long myId);

}
