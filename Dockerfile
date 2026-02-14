FROM gradle:jdk21-alpine AS build
WORKDIR /app

# 라이브러리 의존성 먼저 복사 및 다운로드 (캐싱 활용)
COPY build.gradle settings.gradle ./
RUN gradle build -x test --no-daemon || return 0

# 소스 코드 복사 및 빌드
COPY src ./src
RUN ./gradlew bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

