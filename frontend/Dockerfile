## frontend/Dockerfile

FROM node:24.7.0-alpine3.22 AS deps
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

FROM node:24.7.0-alpine3.22 AS runner
WORKDIR /app
ENV NODE_ENV=production
ENV NEXT_TELEMETRY_DISABLED=1
COPY --from=builder ./public ./public
COPY --from=builder ./.next ./.next
COPY --from=builder ./node_modules ./node_modules

EXPOSE 3000

CMD ["npx", "next", "start", "-p", "3000"]
