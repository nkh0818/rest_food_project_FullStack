# 사용한것

# 페이지 플로우

## 페이지 이동 

### 에러시 

## 지금까지 된거 설명
 - 테스트 성공 API 받아올수있음 안되면 키 오류!
 - vscode로 실행시 mvnw spring-boot:run 
### 테스트시
 - http://localhost:8080/api/test/road 이 링크로 API 키 넣고 테스트 바람
 - 테스트시 mvnw test
 - 테스트시 mvnw test -Dtest=클래스명
## ymal파일 정보
   - db : rest_food_db
   - username: root
   - password: 1234
   - 인증키 한국도로공사API  :  직접 설정!
##  Dependencies

### Spring Boot Starters
* **Spring Web**: RESTful API 개발
* **Spring Data JPA**: MySQL 데이터베이스 연동 및 ORM
* **Spring Security**: 보안 및 인증 처리
* **Validation**: 데이터 유효성 검증
* **Spring Boot DevTools**: 개발 편의 도구

### Database & Library
* **MySQL Driver**: MySQL 커넥터
* **Lombok**: 자바 반복 코드 자동화

### JWT (Json Web Token)
* **jjwt-api**: JWT API 인터페이스
* **jjwt-impl**: JWT 구현체
* **jjwt-jackson**: JWT JSON 처리

---

## 📡 External APIs

### 한국도로공사 휴게소 정보
* **API 상세:** [휴게소별 베스트음식 목록](https://data.ex.co.kr/openapi/basicinfo/openApiInfoM?apiId=0502)
* **참고 링크:** [도로공사 공공데이터 소개](https://data.ex.co.kr/openapi/intro/introduce02)
* **테스트 URL:** [휴게소 베스트음식 조회 API](https://data.ex.co.kr/openapi/basicinfo/openApiInfoM?apiId=0502&serviceType=OPENAPI&keyWord=%ED%9C%B4%EA%B2%8C%EC%86%8C&searchDayFrom=undefined&searchDayTo=undefined&CATEGORY=&GROUP_TR=&sId=645)

### 추가 예정 API
* **카카오맵 API**: [Kakao Developers 바로가기](https://developers.kakao.com/)

### 이전거 Readme2에 있음

필요한것 - 
휴게소 별 베스트 음식 
휴게소 별 음식 종류
휴게소 별 음식리뷰
-
휴게소 정보
휴게소 즐길거리
휴게소이벤트
-
휴게소 기름값
-
전국 특이휴게소 정보
특이휴게소에 뭐있는지