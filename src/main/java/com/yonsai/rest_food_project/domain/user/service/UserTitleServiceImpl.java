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

@Service
@Slf4j
@RequiredArgsConstructor
public class UserTitleServiceImpl implements UserTitleService {

    private final TitleRepository titleRepository;
    private final UserTitleRepository userTitleRepository;

    @Override
    @Transactional
    public void checkAndGrantTitles(User user) {
        // 모든 칭호 가져오기
        List<Title> allTitles = titleRepository.findAll();

        Set<Long> ownedTitleIds = user.getUserTitles().stream()
                .map((UserTitle ut) -> ut.getTitle().getTitleId())
                .collect(Collectors.toSet());

        for (Title title : allTitles) {
            if (ownedTitleIds.contains(title.getTitleId()))
                continue;

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
