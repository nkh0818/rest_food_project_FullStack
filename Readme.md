# 사용한것
 ## 하기전 필수사항 
 - env 파일 만들기 .env.sample 제공
 - DB 켜둬야함


### 지금까지 된거 설명

- 테스트 성공 API 받아올수있음 안되면 키 오류!
- vscode로 실행시 mvnw spring-boot:run

### 테스트시

- http://localhost:8080/api/test/road 이 링크로 API 키 넣고 테스트 바람
- 테스트시 mvnw test
- 테스트시 mvnw test -Dtest=클래스명

## Dependencies

### Spring Boot Starters

- **Spring Web**: RESTful API 개발
- **Spring Data JPA**: MySQL 데이터베이스 연동 및 ORM
- **Spring Security**: 보안 및 인증 처리
- **Validation**: 데이터 유효성 검증
- **Spring Boot DevTools**: 개발 편의 도구

### Database & Library

- **MySQL Driver**: MySQL 커넥터
- **Lombok**: 자바 반복 코드 자동화

### JWT (Json Web Token)

- **jjwt-api**: JWT API 인터페이스
- **jjwt-impl**: JWT 구현체
- **jjwt-jackson**: JWT JSON 처리

 ## 깃 주소!
 - https://github.com/nkh0818/rest_food_project_FullStack
---

## 📡 External APIs

### 한국도로공사 휴게소 정보

- **API 상세:** [휴게소별 베스트음식 목록](https://data.ex.co.kr/openapi/basicinfo/openApiInfoM?apiId=0502)
- **참고 링크:** [도로공사 공공데이터 소개](https://data.ex.co.kr/openapi/intro/introduce02)
- **테스트 URL:** [휴게소 베스트음식 조회 API](https://data.ex.co.kr/openapi/basicinfo/openApiInfoM?apiId=0502&serviceType=OPENAPI&keyWord=%ED%9C%B4%EA%B2%8C%EC%86%8C&searchDayFrom=undefined&searchDayTo=undefined&CATEGORY=&GROUP_TR=&sId=645)

### 추가 예정 API

- **카카오맵 API**: [Kakao Developers 바로가기](https://developers.kakao.com/)

### 이전거 Readme2에 있음

-
-

### 필요한것

```text
 - 회원
- 회원가입/로그인(일반 로그인)
- 마이페이지(레벨/칭호/업적/통계 요약)

2. 경로 생성 & 지도
- 출발지/목적지 입력
- 경로 주변 휴게소 마커 + 리스트 표시
- 휴게소 클릭 시 상세 페이지 이동

휴게소 별 베스트 음식
 메뉴 필터(대표메뉴/가격대/키워드)
휴게소 별 음식 종류
휴게소 별 음식리뷰
-- 후기 작성/수정/삭제
- 간단 평가(좋아요/맛있다/별로다 등 태그)
- 평점(선택)
- 나의 후기 목록

휴게소 정보
휴게소 즐길거리
휴게소이벤트
-
휴게소 기름값
-
전국 특이휴게소 정보
특이휴게소 에 뭐있는지
-

즐겨찾기(찜)
- 휴게소/메뉴 찜
- 찜 목록

. AI 추천
- 나의 방문/후기 기반 추천 휴게소/메뉴
- 추천 이유 1~2줄 요약

. RPG 성장 시스템 (미정)
- 활동(방문/후기/좋아요 등)에 따른 XP 지급
- 레벨업 규칙(레벨 테이블 또는 수식)
- 칭호 지급(업적 달성 시 대표 칭호 설정 가능)

.업적 시스템(미정)
- 첫 방문 / 5곳 방문 / 10곳 방문
- 후기 5개/10개 작성
- 특정 메뉴 카테고리 5회 도전 등

```
