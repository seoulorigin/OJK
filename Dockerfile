# 1. 빌드 단계 (Gradle 최신 버전 + JDK 21)
FROM gradle:jdk21-alpine AS build
WORKDIR /app
COPY . .
# 의존성 설치 및 빌드 (테스트 제외)
RUN ./gradlew clean bootJar -x test --no-daemon

# 2. 실행 단계 (가벼운 JDK 21 실행 환경)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# 빌드된 jar 파일만 가져오기
COPY --from=build /app/build/libs/*.jar app.jar

# 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]