package com.yonsai.rest_food_project.domain.user.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.yonsai.rest_food_project.domain.user.entity.Title;
import com.yonsai.rest_food_project.domain.user.entity.User;
import com.yonsai.rest_food_project.domain.user.entity.UserTitle;
import com.yonsai.rest_food_project.domain.user.repository.TitleRepository;
import com.yonsai.rest_food_project.domain.user.repository.UserTitleRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// [칭호 관리 서비스] 유저의 활동 데이터를 기반으로 획득 가능한 칭호를 검사하고 부여하는 시스템을 담당

@Service
@Slf4j
@RequiredArgsConstructor
public class UserTitleServiceImpl implements UserTitleService {

    private final TitleRepository titleRepository;
    private final UserTitleRepository userTitleRepository;

    //[칭호 부여 및 갱신] 유저의 현재 점수를 모든 칭호 조건과 비교하여, 미보유 칭호 획득 및 대표 칭호 자동 변경을 수행
    @Override
    @Transactional
    public void checkAndGrantTitles(User user) {
        // 1. 시스템에 존재하는 모든 칭호 기준 로드
        List<Title> allTitles = titleRepository.findAll();

        Set<Long> ownedTitleIds = user.getUserTitles().stream()
                .map((UserTitle ut) -> ut.getTitle().getTitleId())
                .collect(Collectors.toSet());

        for (Title title : allTitles) {
            if (ownedTitleIds.contains(title.getTitleId()))
                continue;

            // 3. 획득 조건 검사 (현재는 활동 점수 기반 'SCORE' 방식 위주)
            boolean isEligible = false;
            if ("SCORE".equals(title.getConditionType())) {
                isEligible = user.getActivityScore() >= title.getConditionValue();
            }

            if (isEligible) {
                UserTitle userTitle = UserTitle.builder()
                        .user(user)
                        .title(title)
                        .isRepresentative(false)
                        .build();

                userTitleRepository.save(userTitle);
                user.getUserTitles().add(userTitle);

                // 5. 대표 칭호 자동 갱신: 기존보다 우선순위(Priority)가 높은 칭호일 경우 즉시 교체
                if (user.getCurrentTitle() == null ||
                        (title.getPriority() != null && title.getPriority() > user.getCurrentTitle().getPriority())) {

                    user.setCurrentTitle(title);
                    log.info("디버깅 - 설정하려는 칭호: {}", title.getTitleName());
                    log.info("✨ 유저 [{}]님의 대표 칭호가 [{}]로 변경되었습니다.", user.getNickname(), title.getTitleName());
                }

                log.info("[칭호획득] 유저 [{}]님이 새로운 칭호 [{}]를 획득했습니다!", user.getNickname(), title.getTitleName());
            }
        }
    }

}
