package com.yonsai.rest_food_project.domain.restArea.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonsai.rest_food_project.domain.restArea.dto.RestAreaResponseDto;
import com.yonsai.rest_food_project.domain.restArea.dto.RestAreaWrapperDto;
import com.yonsai.rest_food_project.domain.restArea.service.RestAreaDataService;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.InputStream;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RestAreaDataService restAreaService;

    @Override
    public void run(String... args) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        // 리소스 경로 설정 (src/main/resources 기준)
        ClassPathResource resource = new ClassPathResource("data/rest_area_data.json");

        try (InputStream is = resource.getInputStream()) {
            // 1. JSON 전체를 Wrapper 객체로 먼저 읽습니다.
            RestAreaWrapperDto wrapper = objectMapper.readValue(is, RestAreaWrapperDto.class);

            // 2. Wrapper에서 실제 데이터인 records 리스트만 추출합니다.
            if (wrapper != null && wrapper.getRecords() != null) {
                List<RestAreaResponseDto> dataList = wrapper.getRecords();

                // 3. 서비스에 데이터 주입
                restAreaService.initData(dataList);

                System.out.println("✅ 성공: 휴게소 데이터 " + dataList.size() + "건 로드 완료");
            } else {
                System.err.println("⚠️ 경고: JSON 파일에 'records' 데이터가 없습니다.");
            }

        } catch (Exception e) {
            System.err.println("❌ 실패: 데이터 로드 중 오류 발생 - " + e.getMessage());
            e.printStackTrace(); // 상세한 에러 로그 확인을 위해 추가
        }
    }

}