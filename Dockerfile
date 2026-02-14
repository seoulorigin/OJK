# 1. 빌드 단계
FROM gradle:jdk21-alpine AS build
WORKDIR /app

# gradlew 파일에 실행 권한을 주기 위해 먼저 복사
COPY gradlew gradlew.bat ./
COPY gradle gradle ./
COPY build.gradle settings.gradle ./

# gradlew 권한 부여 및 의존성 다운로드 (소스 없이 빌드 시 발생하는 에러 방지용)
RUN chmod +x gradlew
RUN ./gradlew build -x test --no-daemon || return 0

# 2. 소스 복사 및 실제 빌드
COPY src ./src
RUN ./gradlew bootJar -x test --no-daemon

# 3. 실행 단계
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# 빌드 단계에서 생성된 jar 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]