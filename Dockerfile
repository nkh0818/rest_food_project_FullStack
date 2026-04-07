# 1. 자바 실행 환경(JDK 17)을 베이스 이미지로 사용
FROM openjdk:17-jdk-slim

# 2. 메이븐 빌드로 생성된 jar 파일을 컨테이너 안으로 복사
# (주의: 빌드 후 target 폴더에 생성되는 실제 jar 파일 이름과 맞춰야 합니다)
COPY target/*.jar app.jar

# 3. 서버가 사용할 포트 번호 (보통 8080)
EXPOSE 8080

# 4. 컨테이너가 시작될 때 자바 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]