package com.yonsai.rest_food_project.domain.review.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yonsai.rest_food_project.domain.restArea.entity.RestArea;
import com.yonsai.rest_food_project.domain.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

        // 커뮤니티 전체 피드용
        // 호출할 때 PageRequest.of(0, 10, Sort.by("createdAt").descending())
        Page<Review> findAll(Pageable pageable);

        // 로그인한 유저(blockerId)가 차단한 사람들(blocked.id)의 리뷰를 제외
        @Query("""
                            SELECT r FROM Review r
                            WHERE NOT EXISTS (
                                SELECT b FROM Block b
                                WHERE b.blocker.id = :blockerId
                                AND b.blocked.id = r.user.id
                            )
                        """)
        Page<Review> findAllExcludingBlocked(@Param("blockerId") Long blockerId, Pageable pageable);

        List<Review> findByRestAreaId(Long restAreaId);

        List<Review> findByFoodId(Long foodId);

        List<Review> findByUserId(Long userId);

        // 정렬 포함 조회
        List<Review> findByRestAreaIdOrderByCreatedAtDesc(Long restAreaId);

        List<Review> findByFoodIdOrderByCreatedAtDesc(Long foodId);

        List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);

        // count(집계)
        long countByUserId(Long userId);

        long countByUserIdAndFoodIsNotNull(Long userId);

        long countByUserIdAndImageUrlIsNotNull(Long userId);

        long countByUserIdAndRestAreaId(Long userId, Long restAreaId);

        // 평균 평점
        @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.restArea.id = :restAreaId")
        Double getAverageRating(@Param("restAreaId") Long restAreaId);

        // 좋아요 합
        @Query("SELECT COALESCE(SUM(r.likeCount), 0) FROM Review r WHERE r.user.id = :userId")
        Long sumLikeCountByUserId(@Param("userId") Long userId);

        // 음식 카테고리별 리뷰 개수
        @Query("""
                            select count(r)
                            from Review r
                            where r.user.id = :userId
                            and r.food is not null
                            and r.food.categoryCode = :categoryCode
                        """)
        long countFoodCategoryReviews(Long userId, String categoryCode);

        // 전체 휴게소 개수
        @Query("""
                            select count(distinct r.restArea.id)
                            from Review r
                            where r.user.id = :userId
                        """)
        long countDistinctRestAreaByUserId(@Param("userId") Long userId);

        // 노선별 (IN)
        @Query("""
                            select count(distinct r.restArea.id)
                            from Review r
                            where r.user.id = :userId
                            and r.restArea.routeName IN (:routeNames)
                        """)
        long countDistinctRestAreaByUserIdAndRouteNames(
                        @Param("userId") Long userId,
                        @Param("routeNames") List<String> routeNames);

        // 유저 평균 평점
        @Query("""
                            select coalesce(avg(r.rating), 0)
                            from Review r
                            where r.user.id = :userId
                        """)
        Double findAverageRatingByUserId(Long userId);

        // 휴게소 기준 평균 평점
        @Query("""
                            select coalesce(avg(r.rating), 0)
                            from Review r
                            where r.restArea.id = :restAreaId
                        """)
        Double findAverageRatingByRestAreaId(Long restAreaId);

        // 휴게소 리뷰 개수
        long countByRestAreaId(Long restAreaId);

        // -----0406 나다희-----
        // 좋아요 순으로 정렬
        List<Review> findAllByOrderByLikeCountDesc(Pageable pageable);

        // 리뷰가 최근에 많이 달린 휴게소 Top5
        @Query("SELECT r FROM RestArea r " +
                        "WHERE r IN (SELECT rev.restArea FROM Review rev " +
                        "GROUP BY rev.restArea " +
                        "ORDER BY COUNT(rev) DESC)")
        List<RestArea> findTrendingRestAreas(Pageable pageable);

}