# 项目概述
这是一个前后端分离的多租户内容管理系统（CMS）。每个租户使用一个数据库。
后端使用 Laravel 框架，前端使用 Next.js 框架。数据库使用PostgreSQL。
代理网关使用Traefik，运行时使用Docker compose，启动脚本使用makefile。
鉴权使用无状态Token，前端充分考虑CSR和SSR的优缺点酌情使用。

## 文件夹结构

- `frontend`：前端代码，标准的Next.js项目结构，管理后台页面在`frontend/src/app/admin`目录下，租户页面在`frontend/src/app/tenant`目录下
- `backend`：后端代码，使用Laravel框架，启动脚本在`bin`目录下，其他都是标准的Laravel项目结构
- `traefik`：Traefik配置文件，分了开发环境和生产环境
- `docker-compose.yml`：Docker Compose配置文件
- `Makefile`：Makefile配置文件

## 框架和技术栈

- 前端：Next.js、TypeScript、Tailwind CSS。
- 后端：PHP、Laravel、stancl/tenancy、PostgreSQL、Redis、varnish。
- 代理：Traefik
- 容器化：Docker、Docker Compose
- 脚本：Makefile

## 代码风格

- 参考 `.editorconfig` 文件
- 注释和文档，错误日志等使用日文
- 数据库表和字段、函数、变量、类名等使用英文

## 启动命令

参看 `Makefile`

- 构建镜像: `make build service=($SERVICE_NAME)`
- 启动开发环境：`make up`
- 停止开发环境：`make down`
- 查看日志：`make logs service=($SERVICE_NAME)`
- 进入后端容器：`make exec service=($SERVICE_NAME)`
