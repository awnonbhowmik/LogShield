# ── Deps stage ────────────────────────────────────────────────────────────────
FROM node:20-alpine AS deps
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci --ignore-scripts

# ── Build stage ───────────────────────────────────────────────────────────────
FROM node:20-alpine AS builder
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY . .

# API_URL is used by next.config.ts rewrites at build time — point to backend service
ARG API_URL=http://backend:8080
ENV API_URL=${API_URL}
ENV NEXT_TELEMETRY_DISABLED=1

RUN npm run build

# ── Runtime stage ─────────────────────────────────────────────────────────────
FROM node:20-alpine AS runner
WORKDIR /app

ENV NODE_ENV=production
ENV NEXT_TELEMETRY_DISABLED=1
ENV PORT=3000
ENV HOSTNAME=0.0.0.0

COPY --from=builder /app/.next/standalone ./
COPY --from=builder /app/.next/static   ./.next/static
COPY --from=builder /app/public         ./public

EXPOSE 3000
CMD ["node", "server.js"]
