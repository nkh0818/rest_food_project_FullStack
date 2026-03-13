package com.yonsai.rest_food_project.domain.user.service;

import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.yonsai.rest_food_project.domain.user.dto.UserResponseDTO;
import com.yonsai.rest_food_project.domain.user.entity.User;
import com.yonsai.rest_food_project.domain.user.entity.UserRole;
import com.yonsai.rest_food_project.domain.user.repository.UserRepository;
import com.yonsai.rest_food_project.global.common.NicknameGenerator;
import com.yonsai.rest_food_project.global.exception.RoadQuestException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/** 실제로 repo에 접근해서 데이터를 다루는 클래스로만 사용합니다
 *  로직에서 repo에 접근해 조회 결과가 있어야지만 다음 로직을 실행할 수 있는 경우에는
 *  무조건 .orElseThrow를 통해 exception을 잡아 주세요!
 */

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final NicknameGenerator nicknameGenerator;

    @Override
    @Transactional
    public User saveOrUpdate(String email, String nickname, String provider, String providerId){
        //upsert : 없으면 insert, 있으면 update
        return userRepo.findByProviderAndProviderId(provider, providerId)
                .map(user-> {
                    // 존재하면 정보를 update
                    user.setEmail(email);
                    return user;
                })
                .orElseGet(()->{
                    // 없으면 insert

                    String randomNickname = generateUniqueNickname();

                    User newUser = User.builder()
                                .email(email)
                                .nickname(randomNickname)
                                .provider(provider)
                                .providerId(providerId)
                                .role(UserRole.USER)
                                .password("1234") //임시 비밀번호
                                .build();
                    return userRepo.save(newUser);
                });
    }

    private String generateUniqueNickname(){

        String nickname = Stream.generate(nicknameGenerator::generate)
                        .filter(name -> !userRepo.existsByNickname(name)) //중복체크
                        .findFirst()
                        .orElseThrow(()-> new RoadQuestException("닉네임 생성에 실패했습니다."));
        return nickname;
    }

    @Override
    public UserResponseDTO getMyInfo(Long userId){
        User user = userRepo.findById(userId)
                    .orElseThrow(()-> new RoadQuestException("사용자를 찾을 수 없습니다. ID: " + userId));
        return UserResponseDTO.from(user);
    }

    @Override
    @Transactional // 데이터를 수정하기 위한 권한 부여
    public void nicknameUpdate(Long userId, String newNickname){
        
        // 중복확인
        if(userRepo.existsByNickname(newNickname)){
            throw new RoadQuestException("이미 사용중인 닉네임입니다!");
        }

        User user = userRepo.findById(userId)
                    .orElseThrow(()-> new RoadQuestException("사용자를 찾을 수 없습니다. ID: " + userId));

        user.setNickname(newNickname);        
    };

}
