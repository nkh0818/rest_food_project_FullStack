package com.yonsai.rest_food_project.domain.review.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; 
import org.springframework.data.repository.query.Param;

import com.yonsai.rest_food_project.domain.review.entity.ReviewLike;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

    boolean existsByReviewIdAndUserId(Long reviewId, Long userId);

    Optional<ReviewLike> findByReviewIdAndUserId(Long reviewId, Long userId);

    long countByReviewId(Long reviewId);
    
    @Query("SELECT rl.review.id FROM ReviewLike rl WHERE rl.user.id = :userId")
    List<Long> findReviewIdsByUserId(@Param("userId") Long userId);
}
