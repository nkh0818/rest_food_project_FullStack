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

}