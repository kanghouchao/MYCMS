
# 1. 依存関係をダウンロードしてキャッシュします
FROM gradle:9.0.0-jdk24-ubi-minimal AS dependencies
WORKDIR /app
COPY build.gradle settings.gradle ./
RUN --mount=type=cache,target=/home/gradle/.gradle \
    --mount=type=cache,target=/app/.gradle \
	gradle dependencies --configuration runtimeClasspath --no-daemon

# 2. テストを実行します
FROM dependencies AS test
WORKDIR /app
COPY src src
RUN --mount=type=cache,target=/home/gradle/.gradle \
    --mount=type=cache,target=/app/.gradle \
	gradle test --no-daemon --info

# 3. ビルド成果物を作成します
FROM dependencies AS build
WORKDIR /app
COPY src src
RUN --mount=type=cache,target=/home/gradle/.gradle \
    --mount=type=cache,target=/app/.gradle \
	gradle build --no-daemon --info -x test

# 4. ランタイムイメージを作成します
FROM eclipse-temurin:21-jre-alpine-3.22
WORKDIR /app
COPY --from=build /app/build/libs/cms-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
