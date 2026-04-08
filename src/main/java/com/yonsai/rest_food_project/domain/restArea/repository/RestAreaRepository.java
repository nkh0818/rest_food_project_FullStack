package com.yonsai.rest_food_project.domain.restArea.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yonsai.rest_food_project.domain.restArea.entity.RestArea;

public interface RestAreaRepository extends JpaRepository<RestArea, Long> {

       // 기본 검색 (리스트 & 페이징)
       List<RestArea> findByNameContaining(String keyword);

       Page<RestArea> findByNameContaining(String keyword, Pageable pageable);

       // 랜덤 정렬
       @Query(value = "SELECT * FROM rest_area WHERE gasoline_price > 0 ORDER BY RAND()", nativeQuery = true)
       List<RestArea> findRandomAreas(Pageable pageable);

       // 내 위치 기반 후보지 검색
       @Query("SELECT r FROM RestArea r WHERE " +
                     "r.latitude BETWEEN :minLat AND :maxLat AND " +
                     "r.longitude BETWEEN :minLng AND :maxLng AND " +
                     "r.gasolinePrice > 0")
       List<RestArea> findCandidateAreas(@Param("minLat") Double minLat, @Param("maxLat") Double maxLat,
                     @Param("minLng") Double minLng, @Param("maxLng") Double maxLng);

       boolean existsByStdRestCd(String stdRestCd);

       Optional<RestArea> findByStdRestCd(String stdRestCd);

       List<RestArea> findByNameContainingOrRouteNameContaining(String name, String routeName);

       List<RestArea> findByRouteNameContaining(String routeName);

       List<RestArea> findTop5ByGasolinePriceGreaterThanOrderByGasolinePriceAsc(Double minPrice);

       // --- Ai 가이드 ---

       // 1. AI 분석 점수(aiScore)가 높은 순으로 상위 N개 가져오기
       List<RestArea> findTop20ByOrderByAiScoreDesc();

       // 2. 특정 태그(경치, 맛집, 테마 등)가 포함된 휴게소 검색
       List<RestArea> findByAiTagsContaining(String tag);

       // 3. 평점이 일정 수준 이상이면서 휘발유 가격 정보가 있는 곳 (안전한 추천용)
       List<RestArea> findByRatingGreaterThanEqualAndGasolinePriceGreaterThan(Double rating, Double minPrice);

       // 4. 루트 이름으로 찾되, AI 점수가 높은 순으로 정렬 (경로 기반 추천 시 유용)
       List<RestArea> findByRouteNameContainingOrderByAiScoreDesc(String routeName);

       // 5. (Native Query) AI 점수 + 별점을 조합해서 랜덤하게 상위 후보군 뽑기
       @Query(value = "SELECT * FROM rest_area WHERE ai_score >= 4.0 OR rating >= 4.0 ORDER BY RAND() LIMIT 10", nativeQuery = true)
       List<RestArea> findBestCandidateAreas();

}