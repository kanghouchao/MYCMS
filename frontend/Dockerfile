## frontend/Dockerfile

FROM node:24.6.0-alpine3.22 AS base
WORKDIR /app

FROM base AS deps
COPY package.json ./
RUN --mount=type=cache,target=/root/.npm \
    --mount=type=cache,target=/root/.cache \
    npm install --no-audit --no-fund

FROM deps AS test
ENV NEXT_TELEMETRY_DISABLED=1
COPY . .
RUN --mount=type=cache,target=/root/.npm \
    --mount=type=cache,target=/root/.cache \
    npm run test --no-watch --ci --reporters=default --reporters=jest-junit

FROM deps AS builder
ENV NEXT_TELEMETRY_DISABLED=1
COPY . .
RUN --mount=type=cache,target=/root/.npm \
    --mount=type=cache,target=/root/.cache \
    npm run build && \
    npm prune --production

FROM node:24.6.0-alpine3.22 AS runner
WORKDIR /app
ENV NODE_ENV=production
ENV NEXT_TELEMETRY_DISABLED=1
COPY --from=builder /app/public ./public
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/node_modules ./node_modules

CMD ["npx", "next", "start", "-p", "3000"]
