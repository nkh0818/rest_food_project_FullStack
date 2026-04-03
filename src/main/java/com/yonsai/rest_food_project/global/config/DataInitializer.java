package com.yonsai.rest_food_project.global.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.yonsai.rest_food_project.domain.restArea.entity.FoodCategory;
import com.yonsai.rest_food_project.domain.restArea.entity.FoodNameMapping;
import com.yonsai.rest_food_project.domain.restArea.repository.FoodCategoryRepository;
import com.yonsai.rest_food_project.domain.restArea.repository.FoodNameMappingRepository;
import com.yonsai.rest_food_project.domain.user.entity.Reward;
import com.yonsai.rest_food_project.domain.user.entity.Title;
import com.yonsai.rest_food_project.domain.user.entity.User;
import com.yonsai.rest_food_project.domain.user.entity.UserRole;
import com.yonsai.rest_food_project.domain.user.repository.RewardRepository;
import com.yonsai.rest_food_project.domain.user.repository.TitleRepository;
import com.yonsai.rest_food_project.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TitleRepository titleRepository;
    private final RewardRepository rewardRepository;
    private final FoodCategoryRepository foodCategoryRepository;
    private final FoodNameMappingRepository foodNameMappingRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("==== 초기 데이터 세팅 시작 ====");

        initTitles();
        initRewards();
        initFoodCategories();
        initFoodNameMappings();

        log.info("==== 초기 데이터 세팅 완료 ====");
    }

    private void initTitles() {
        log.info(">> Title 초기화 시작");

        saveTitleIfNotExists("초보 탐험가", "서비스 적응 단계", "SCORE", "SCORE", 50, null, null, 10);
        saveTitleIfNotExists("리뷰 탐험가", "리뷰 활동 시작", "SCORE", "SCORE", 120, null, null, 20);
        saveTitleIfNotExists("휴게소 기록자", "꾸준한 기록 유저", "SCORE", "SCORE", 250, null, null, 30);
        saveTitleIfNotExists("휴게소 전문가", "경험 많은 활동 유저", "SCORE", "SCORE", 450, null, null, 40);
        saveTitleIfNotExists("로드마스터", "핵심 활동 유저", "SCORE", "SCORE", 700, null, null, 50);
        saveTitleIfNotExists("전설의 여행자", "최상위 활동 유저", "SCORE", "SCORE", 1200, null, null, 60);

        saveTitleIfNotExists("첫 방문자", "첫 리뷰 작성", "REVIEW", "REVIEW_COUNT", 1, null, null, 11);
        saveTitleIfNotExists("길 위의 기록자", "리뷰 10개 작성", "REVIEW", "REVIEW_COUNT", 10, null, null, 21);
        saveTitleIfNotExists("리뷰 마스터", "리뷰 50개 작성", "REVIEW", "REVIEW_COUNT", 50, null, null, 31);

        saveTitleIfNotExists("첫 인증자", "사진 리뷰 1개", "PHOTO", "PHOTO_REVIEW_COUNT", 1, null, null, 12);
        saveTitleIfNotExists("여행 사진가", "사진 리뷰 5개", "PHOTO", "PHOTO_REVIEW_COUNT", 5, null, null, 22);
        saveTitleIfNotExists("휴게소 포토그래퍼", "사진 리뷰 15개", "PHOTO", "PHOTO_REVIEW_COUNT", 15, null, null, 32);
        saveTitleIfNotExists("현장 기록 마스터", "사진 리뷰 30개", "PHOTO", "PHOTO_REVIEW_COUNT", 30, null, null, 42);

        saveTitleIfNotExists("공감받는 리뷰어", "좋아요 10개", "LIKE", "LIKE_COUNT", 10, null, null, 13);
        saveTitleIfNotExists("인기 리뷰어", "좋아요 50개", "LIKE", "LIKE_COUNT", 50, null, null, 23);
        saveTitleIfNotExists("믿고 보는 리뷰어", "좋아요 100개", "LIKE", "LIKE_COUNT", 100, null, null, 33);
        saveTitleIfNotExists("커뮤니티 스타", "좋아요 130개", "LIKE", "LIKE_COUNT", 130, null, null, 43);

        saveTitleIfNotExists("초보 정복자", "서로 다른 휴게소 3곳 리뷰", "REST_AREA", "DISTINCT_REST_AREA_COUNT", 3, null, null, 14);
        saveTitleIfNotExists("길 위의 탐험가", "서로 다른 휴게소 10곳 리뷰", "REST_AREA", "DISTINCT_REST_AREA_COUNT", 10, null, null,
                24);
        saveTitleIfNotExists("전국 휴게소 도전자", "서로 다른 휴게소 20곳 리뷰", "REST_AREA", "DISTINCT_REST_AREA_COUNT", 20, null, null,
                34);
        saveTitleIfNotExists("로드 정복자", "서로 다른 휴게소 40곳 리뷰", "REST_AREA", "DISTINCT_REST_AREA_COUNT", 40, null, null, 44);

        saveTitleIfNotExists("경부 라인 탐험가", "경부고속도로 휴게소 5곳 리뷰", "ROUTE", "ROUTE_REVIEW_COUNT", 5, null, "경부선", 15);
        saveTitleIfNotExists("서해안 정복자", "서해안고속도로 휴게소 5곳 리뷰", "ROUTE", "ROUTE_REVIEW_COUNT", 5, null, "서해안선", 25);
        saveTitleIfNotExists("영동 로드러너", "영동고속도로 휴게소 5곳 리뷰", "ROUTE", "ROUTE_REVIEW_COUNT", 5, null, "영동선", 35);
        saveTitleIfNotExists("남해 길잡이", "남해고속도로 휴게소 5곳 리뷰", "ROUTE", "ROUTE_REVIEW_COUNT", 5, null, "남해선", 45);

        saveTitleIfNotExists("돈까스 입문자", "돈까스 리뷰 3회", "FOOD", "FOOD_CATEGORY_COUNT", 3, "PORK_CUTLET", null, 16);
        saveTitleIfNotExists("돈까스 탐험가", "돈까스 리뷰 7회", "FOOD", "FOOD_CATEGORY_COUNT", 7, "PORK_CUTLET", null, 26);
        saveTitleIfNotExists("돈까스 헌터", "돈까스 리뷰 15회", "FOOD", "FOOD_CATEGORY_COUNT", 15, "PORK_CUTLET", null, 36);

        saveTitleIfNotExists("제육 입문자", "제육 리뷰 3회", "FOOD", "FOOD_CATEGORY_COUNT", 3, "SPICY_PORK", null, 17);
        saveTitleIfNotExists("제육 탐험가", "제육 리뷰 7회", "FOOD", "FOOD_CATEGORY_COUNT", 7, "SPICY_PORK", null, 27);
        saveTitleIfNotExists("제육 헌터", "제육 리뷰 15회", "FOOD", "FOOD_CATEGORY_COUNT", 15, "SPICY_PORK", null, 37);

        saveTitleIfNotExists("면 요리 애호가", "면류 리뷰 3회", "FOOD", "FOOD_CATEGORY_COUNT", 3, "NOODLE", null, 18);
        saveTitleIfNotExists("면 요리 탐험가", "면류 리뷰 7회", "FOOD", "FOOD_CATEGORY_COUNT", 7, "NOODLE", null, 28);
        saveTitleIfNotExists("면 요리 마스터", "면류 리뷰 15회", "FOOD", "FOOD_CATEGORY_COUNT", 15, "NOODLE", null, 38);

        saveTitleIfNotExists("뜨끈한 한 끼 입문자", "국밥/탕류 리뷰 3회", "FOOD", "FOOD_CATEGORY_COUNT", 3, "SOUP_STEW", null, 19);
        saveTitleIfNotExists("국물 탐험가", "국밥/탕류 리뷰 7회", "FOOD", "FOOD_CATEGORY_COUNT", 7, "SOUP_STEW", null, 29);
        saveTitleIfNotExists("국밥 장인", "국밥/탕류 리뷰 15회", "FOOD", "FOOD_CATEGORY_COUNT", 15, "SOUP_STEW", null, 39);

        saveTitleIfNotExists("한 끼 해결사", "덮밥/정식류 리뷰 3회", "FOOD", "FOOD_CATEGORY_COUNT", 3, "RICE_MEAL", null, 20);
        saveTitleIfNotExists("든든한 식사 탐험가", "덮밥/정식류 리뷰 7회", "FOOD", "FOOD_CATEGORY_COUNT", 7, "RICE_MEAL", null, 30);
        saveTitleIfNotExists("정식 마스터", "덮밥/정식류 리뷰 15회", "FOOD", "FOOD_CATEGORY_COUNT", 15, "RICE_MEAL", null, 40);

        saveTitleIfNotExists("간식 수집가", "간식류 리뷰 3회", "FOOD", "FOOD_CATEGORY_COUNT", 3, "SNACK", null, 21);
        saveTitleIfNotExists("간식 탐험가", "간식류 리뷰 7회", "FOOD", "FOOD_CATEGORY_COUNT", 7, "SNACK", null, 31);
        saveTitleIfNotExists("휴게소 간식 헌터", "간식류 리뷰 15회", "FOOD", "FOOD_CATEGORY_COUNT", 15, "SNACK", null, 41);

        log.info(">> Title 초기화 완료");
    }

    private void initRewards() {
        log.info(">> Reward 초기화 시작");

        saveRewardIfNotExists("휴게소 커피 1,000원 할인 쿠폰", "휴게소 제휴 매장에서 사용하는 가상 쿠폰", "COUPON", 100, "COFFEE");
        saveRewardIfNotExists("주유 2,000원 할인 쿠폰", "주유 할인용 가상 쿠폰", "COUPON", 300, "OIL");
        saveRewardIfNotExists("간식 교환 쿠폰", "휴게소 간식 교환용 가상 쿠폰", "COUPON", 150, "SNACK");
        saveRewardIfNotExists("인기 리뷰어 배지", "인기 리뷰어 전용 보상", "BADGE", 200, "BADGE");
        saveRewardIfNotExists("음식 전문가 마크", "음식 리뷰 활동 보상", "MARK", 250, "FOOD");
        saveRewardIfNotExists("포토 리뷰어 마크", "사진 리뷰 활동 보상", "MARK", 250, "PHOTO");

        log.info(">> Reward 초기화 완료");
    }

    private void initFoodCategories() {
        log.info(">> FoodCategory 초기화 시작");

        saveFoodCategoryIfNotExists("PORK_CUTLET", "돈까스", "왕돈까스, 치즈돈까스, 수제돈까스");
        saveFoodCategoryIfNotExists("SPICY_PORK", "제육", "제육볶음, 제육덮밥");
        saveFoodCategoryIfNotExists("NOODLE", "면류", "우동, 라면, 국수");
        saveFoodCategoryIfNotExists("SOUP_STEW", "국밥/탕류", "국밥, 순두부, 해장국, 설렁탕");
        saveFoodCategoryIfNotExists("RICE_MEAL", "덮밥/정식류", "비빔밥, 덮밥, 정식");
        saveFoodCategoryIfNotExists("SNACK", "간식류", "호두과자, 핫도그, 소떡소떡");
        saveFoodCategoryIfNotExists("KOREAN_SET", "한식대표메뉴", "찌개류, 불고기류");
        saveFoodCategoryIfNotExists("WESTERN_FAST", "양식/패스트", "햄버거, 토스트, 피자류");
        saveFoodCategoryIfNotExists("ETC", "기타", "분류되지 않은 음식");

        log.info(">> FoodCategory 초기화 완료");
    }

    private void initFoodNameMappings() {
        log.info(">> FoodNameMapping 초기화 시작");

        saveFoodMappingIfNotExists("돈까스", "돈까스", "PORK_CUTLET");
        saveFoodMappingIfNotExists("왕돈까스", "돈까스", "PORK_CUTLET");
        saveFoodMappingIfNotExists("치즈돈까스", "돈까스", "PORK_CUTLET");
        saveFoodMappingIfNotExists("수제돈까스", "돈까스", "PORK_CUTLET");
        saveFoodMappingIfNotExists("등심돈까스", "돈까스", "PORK_CUTLET");
        saveFoodMappingIfNotExists("안심돈까스", "돈까스", "PORK_CUTLET");

        saveFoodMappingIfNotExists("제육", "제육", "SPICY_PORK");
        saveFoodMappingIfNotExists("제육볶음", "제육", "SPICY_PORK");
        saveFoodMappingIfNotExists("제육덮밥", "제육", "SPICY_PORK");

        saveFoodMappingIfNotExists("우동", "우동", "NOODLE");
        saveFoodMappingIfNotExists("가락우동", "우동", "NOODLE");
        saveFoodMappingIfNotExists("해물우동", "우동", "NOODLE");
        saveFoodMappingIfNotExists("라면", "라면", "NOODLE");
        saveFoodMappingIfNotExists("잔치국수", "국수", "NOODLE");
        saveFoodMappingIfNotExists("국수", "국수", "NOODLE");
        saveFoodMappingIfNotExists("냉면", "냉면", "NOODLE");

        saveFoodMappingIfNotExists("국밥", "국밥", "SOUP_STEW");
        saveFoodMappingIfNotExists("소고기국밥", "국밥", "SOUP_STEW");
        saveFoodMappingIfNotExists("돼지국밥", "국밥", "SOUP_STEW");
        saveFoodMappingIfNotExists("순두부", "순두부", "SOUP_STEW");
        saveFoodMappingIfNotExists("해장국", "해장국", "SOUP_STEW");
        saveFoodMappingIfNotExists("설렁탕", "설렁탕", "SOUP_STEW");
        saveFoodMappingIfNotExists("갈비탕", "갈비탕", "SOUP_STEW");
        saveFoodMappingIfNotExists("김치찌개", "김치찌개", "SOUP_STEW");
        saveFoodMappingIfNotExists("된장찌개", "된장찌개", "SOUP_STEW");

        saveFoodMappingIfNotExists("비빔밥", "비빔밥", "RICE_MEAL");
        saveFoodMappingIfNotExists("덮밥", "덮밥", "RICE_MEAL");
        saveFoodMappingIfNotExists("불고기덮밥", "불고기덮밥", "RICE_MEAL");
        saveFoodMappingIfNotExists("백반", "백반", "RICE_MEAL");
        saveFoodMappingIfNotExists("정식", "정식", "RICE_MEAL");
        saveFoodMappingIfNotExists("카레덮밥", "카레덮밥", "RICE_MEAL");

        saveFoodMappingIfNotExists("호두과자", "호두과자", "SNACK");
        saveFoodMappingIfNotExists("핫도그", "핫도그", "SNACK");
        saveFoodMappingIfNotExists("소떡소떡", "소떡소떡", "SNACK");
        saveFoodMappingIfNotExists("핫바", "핫바", "SNACK");
        saveFoodMappingIfNotExists("어묵", "어묵", "SNACK");
        saveFoodMappingIfNotExists("떡볶이", "떡볶이", "SNACK");

        saveFoodMappingIfNotExists("불고기", "불고기", "KOREAN_SET");
        saveFoodMappingIfNotExists("갈비", "갈비", "KOREAN_SET");
        saveFoodMappingIfNotExists("청국장", "청국장", "KOREAN_SET");

        saveFoodMappingIfNotExists("햄버거", "햄버거", "WESTERN_FAST");
        saveFoodMappingIfNotExists("버거", "햄버거", "WESTERN_FAST");
        saveFoodMappingIfNotExists("토스트", "토스트", "WESTERN_FAST");
        saveFoodMappingIfNotExists("피자", "피자", "WESTERN_FAST");
        saveFoodMappingIfNotExists("샌드위치", "샌드위치", "WESTERN_FAST");

        log.info(">> FoodNameMapping 초기화 완료");
    }

    private void saveTitleIfNotExists(String titleName, String description, String titleType,
            String conditionType, Integer conditionValue,
            String categoryCode, String routeName, Integer priority) {

        boolean exists = titleRepository.findByTitleName(titleName).isPresent();

        if (exists) {
            return;
        }

        Title title = Title.builder()
                .titleName(titleName)
                .description(description)
                .titleType(titleType)
                .conditionType(conditionType)
                .conditionValue(conditionValue)
                .categoryCode(categoryCode)
                .routeName(routeName)
                .priority(priority)
                .isActive(true)
                .build();

        titleRepository.save(title);
        log.info("칭호 저장 완료: {}", titleName);
    }

    private void saveRewardIfNotExists(String rewardName, String description,
            String rewardType, Integer pointCost, String couponCodePrefix) {

        boolean exists = rewardRepository.findByRewardName(rewardName).isPresent();

        if (exists) {
            return;
        }

        Reward reward = Reward.builder()
                .rewardName(rewardName)
                .description(description)
                .rewardType(rewardType)
                .pointCost(pointCost)
                .couponCodePrefix(couponCodePrefix)
                .active(true)
                .build();

        rewardRepository.save(reward);
        log.info("보상 저장 완료: {}", rewardName);
    }

    private void saveFoodCategoryIfNotExists(String code, String name, String description) {
        if (foodCategoryRepository.existsById(code)) {
            return;
        }

        FoodCategory category = FoodCategory.builder()
                .categoryCode(code)
                .categoryName(name)
                .description(description)
                .isActive(true)
                .build();

        foodCategoryRepository.save(category);
        log.info("음식 카테고리 저장 완료: {}", name);
    }

    private void saveFoodMappingIfNotExists(String keyword, String normalizedName, String categoryCode) {
        // findAll() 대신 existsByKeyword 사용! 0402나다희
        if (foodNameMappingRepository.existsByKeyword(keyword)) {
            return;
        }

        FoodNameMapping mapping = FoodNameMapping.builder()
                .keyword(keyword)
                .normalizedName(normalizedName)
                .categoryCode(categoryCode)
                .description(keyword + " -> " + categoryCode)
                .build();

        foodNameMappingRepository.save(mapping);
    }
}