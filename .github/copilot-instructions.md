# 项目概述
这是一个前后端分离的多租户内容管理系统（CMS）。租户共用数据库。
后端使用 Java 21 和 Spring boot 框架，前端使用 TypeScript 和 Next.js。数据库使用PostgreSQL。
代理网关使用Traefik，运行时使用Docker compose，启动脚本使用makefile。
鉴权使用无状态Token，前端充分考虑CSR和SSR的优缺点酌情使用。

## 文件夹结构

- `frontend`：前端代码，标准的Next.js项目结构，管理后台页面在`frontend/src/app/central`目录下，租户页面在`frontend/src/app/tenant`目录下
- `backend`：后端代码，使用Java 21和Spring Boot框架，管理后台API在`backend/src/main/java/com/cms/central`，租户API在`backend/src/main/java/com/cms/tenant`
- `traefik`：Traefik配置文件，分了开发环境和生产环境
- `docker-compose.yml`：Docker Compose配置文件
- `Makefile`：Makefile配置文件

## 框架和技术栈

- 前端：Node JS、Next.js、TypeScript、Tailwind CSS、npm。
- 后端：Java 21、Spring Boot、Spring Security、liquibase、PostgreSQL、Redis、Gradle。
- 代理：Traefik
- 容器化：Docker、Docker Compose
- 脚本：Makefile

## 代码风格

- 参考 `.editorconfig` 文件
- 注释和文档，错误日志等使用日文
- 数据库表和字段、函数、变量、类名等使用英文

## 启动命令

参看 `Makefile`

- 构建镜像: `make build service=($service)`
- 运行测试: `make test service=($service)`
- 启动开发环境：`make up`
- 停止开发环境：`make down`
- 查看日志：`make logs service=($service)`
- 进入后端容器：`make exec service=($service)`
