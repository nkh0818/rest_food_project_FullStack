# 1. 자바 실행 환경(JDK 17) 변경
FROM eclipse-temurin:17-jdk-alpine

# 2. 메이븐 빌드로 생성된 jar 파일을 컨테이너 안으로 복사
# 로그를 보니 파일명이 rest_food_project-0.0.1-SNAPSHOT.jar 입니다.
COPY target/*.jar app.jar

# 3. 서버가 사용할 포트 번호
EXPOSE 8080

# 4. 컨테이너가 시작될 때 자바 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]