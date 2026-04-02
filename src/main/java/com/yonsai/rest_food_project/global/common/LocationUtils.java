package com.yonsai.rest_food_project.global.common;

import org.springframework.stereotype.Component;

@Component
public class LocationUtils {

    // 지구 반지름 (단위: km)
    private static final double EARTH_RADIUS = 6371;

    /**
     * 두 지점 간의 거리를 계산 (단위: m)
     */
    public double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        // km를 m로 변환하여 반환
        return EARTH_RADIUS * c * 1000;
    }

    // 사용할 때는 각 service에서 private final을 이용한 의존성 주입 -> .getDistance에 유저의 위도경도와 RestArea entity의 위도경도를 불러오세요!
    

}
