# 1. 빌드 단계
FROM gradle:jdk21-alpine AS build
WORKDIR /app

# Gradle 래퍼 및 설정 파일 복사
# 폴더 구조를 유지하기 위해 경로를 정확히 지정합니다.
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle .
COPY settings.gradle .

# 실행 권한 부여 및 의존성 사전 다운로드
RUN chmod +x gradlew
RUN ./gradlew build -x test --no-daemon || return 0

# 2. 소스 코드 복사 및 실제 빌드
COPY src ./src
RUN ./gradlew bootJar -x test --no-daemon

# 3. 실행 단계
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]