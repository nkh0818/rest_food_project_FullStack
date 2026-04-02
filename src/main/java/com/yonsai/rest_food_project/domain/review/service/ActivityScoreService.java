package com.yonsai.rest_food_project.domain.review.service;

import com.yonsai.rest_food_project.domain.review.entity.Review;
import com.yonsai.rest_food_project.domain.user.entity.User;

public interface ActivityScoreService {

    int addScoreForCreatedReview(User user, Review review);

    void addScoreForLikeReceived(User user);

    void addScoreForLikeGiven(User user);

    void subtractScoreForConfirmedReport(User user);

    Double getAverageRating(Long restAreaId);

    Long getReviewCount(Long restAreaId);
}
