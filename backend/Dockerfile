
# 1. 下载依赖并缓存
FROM gradle:9.0.0-jdk24-ubi-minimal AS dependencies
WORKDIR /app
COPY build.gradle settings.gradle ./
RUN --mount=type=cache,target=/home/gradle/.gradle \
    --mount=type=cache,target=/app/.gradle \
	gradle dependencies --configuration runtimeClasspath --no-daemon

# 2. 运行测试
FROM dependencies AS test
WORKDIR /app
COPY src src
RUN --mount=type=cache,target=/home/gradle/.gradle \
    --mount=type=cache,target=/app/.gradle \
	gradle test --no-daemon --info

# 3. 构建产物
FROM dependencies AS build
WORKDIR /app
COPY src src
RUN --mount=type=cache,target=/home/gradle/.gradle \
    --mount=type=cache,target=/app/.gradle \
	gradle build --no-daemon --info -x test

# 4. 构建运行时镜像
FROM eclipse-temurin:21-jre-alpine-3.22
WORKDIR /app
COPY --from=build /app/build/libs/cms-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
