# 빌드
FROM openjdk:21-jdk-slim AS builder
WORKDIR /app
COPY gradlew .
COPY gradle gradle
RUN chmod +x gradlew
COPY build.gradle settings.gradle ./
RUN ./gradlew dependencies --no-daemon || return 0
COPY . .
RUN ./gradlew clean build -x test --no-daemon

# 실행
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
