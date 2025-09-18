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
	# Run unit tests, generate Jacoco reports, and enforce coverage thresholds
	gradle clean test jacocoTestReport jacocoTestCoverageVerification --no-daemon --info

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

# ビルド時にデフォルトJVMオプションを上書き可能にします（例: -Xms256m -Xmx512m）
ARG APP_JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseContainerSupport"
ENV JAVA_OPTS=${APP_JAVA_OPTS}

# ヘルスチェックで使用する curl をインストールします
RUN apk add --no-cache curl

COPY --from=build /app/build/libs/cms-*.jar app.jar
EXPOSE 8080

# ヘルスチェック: Spring Boot Actuator の /actuator/health を確認します
HEALTHCHECK --interval=10s --timeout=3s --start-period=10s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
