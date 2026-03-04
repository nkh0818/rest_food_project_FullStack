# 원래 하려던거 대강 정리 - 여기서 추가하거나 뺴오거나 이걸로 바꿔도댐
벳지 ㅡ 칭호 ㅡ 레벨 ㅡ 빈지도채우기(여행지 기록) ㅡ AI여행지 추천받기 ㅡ 다녀온 여행지 리뷰 (글쓰기)ㅡ
ㅡ 여행날 날씨 ㅡ 여행지 공유,  카카오톡 공유? ㅡ 거리계산 ㅡ 가는길 지도?  ㅡ 여행지 설명(디테일페이지)ㅡ
블로그같이 글쓰기기능 ? 

## 필수정보
- yaml 파일 설정
- MySQL 사용
- 사용 DB 명 TourList
- 계정 아이디 root
- 비밀번호 1234
### API 사이트
 - 한국관광공사 https://www.data.go.kr/data/15101578/openapi.do 
 - 기상청 중기 예보 https://www.data.go.kr/data/15059468/openapi.do

## 정보 VSCODE 사용시 메이븐 실행방법!
- mvnw spring-boot:run             // 스프링 실행
- mvnw test                        // 모든테스트 실행

## 해야할거 필터링 걸기
 - 지역 필터링 걸고  숙박필터링에 종류 여러개있는거 필터링 걸수있게 
 - 여행지도 마찬가지

# 서비스 
 - 페이지 나누기 - React 
 - Header Nav 기능 로그인바 
 - Footer 정보 설명

## 페이지
 - 로그인 페이지 
 - 아이디 , 비밀번호 찾기 
 - 회원가입 페이지 - 
 - 메인 본인 여행지 or 여행 선택 추천받기 보여주는곳(AI? 혹은 자체정보 필터링으로 받기)
 - 지도페이지 지도에서 여행지 선택 가능하게 
 - 여행지 (상세 페이지) ? 
 - 여행지 내가 저장한 여행정보 페이지
    ㄴ여행페이지에서 여행지 공유 
        ㄴ 카카오톡으로 보내기
        ㄴ 여행 메모 설명 쓰기
        ㄴ 삭제 기능
        ㄴ 여행지 거리 계산
        ㄴ 가는길 네비? 지도?

 - 여행지 추가페이지
 - 여행지 수정페이지
 - 내정보 페이지 
        ㄴ정보 페이지(뱃지 칭호 레벨 빈 지도(기록) 내가쓴 리뷰보기 리뷰 삭제)
 - 리뷰 달떄 / 정보볼떄 ? 뱃지 칭호  두개 보이게 혹은 둘중 하나 보이게
  - Ex__ 초보여행자 , 중급 여행자 , 고수 여행자 같이- 랭크 몇개 달기
  - 칭호? 같은거는 레벨 + 빈 지도 채우면 ? 혹은 같은 지역 여행많이 가면 주는거처럼


## 정보
SpringBoot 다운 받은거 

- Spring Web 
- Spring Boot 
- DevTools 
- MySQL Driver 
- Spring Data JPA 
- Lombok
- Thymeleaf 
- Spring Security 
- Azure OpenAI

## 📂 프로젝트 구조 (File Structure)

### 🟢 Java Source (src/main/java)
```text
 src/main/java/com/trip/quest
   ├── config             # Security, AI, API 설정 파일
   ├── controller         # URL 매핑 (WebController, ApiController)
   ├── service            # 비즈니스 로직 (AiService, ExpService)
   ├── repository         # JPA 인터페이스 (DB 접근)
   ├── entity             # DB 테이블 매핑 객체 (User, Plan, Spot)
   ├── dto                # 데이터 전송 객체 (Request/Response)
   └── common             # 공통 유틸리티 (날씨 변환기, 거리 계산기)

 src/main/resources
   ├── mapper             # (MyBatis 사용 시) XML 쿼리 파일
   ├── static             # CSS, JS, 이미지(캐릭터, 뱃지)
   └── templates          # Thymeleaf HTML 파일들
       ├── layout         # 공통 레이아웃 (header, footer)
       ├── main           # 메인 대시보드
       └── plan           # 일정 생성 및 기록 페이지
 
```
