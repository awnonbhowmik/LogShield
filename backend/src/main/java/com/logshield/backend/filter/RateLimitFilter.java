package com.logshield.backend.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(1)
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_UPLOADS_PER_MINUTE = 20;
    private static final long WINDOW_MS = 60_000;

    private final ConcurrentHashMap<String, Deque<Long>> windowMap = new ConcurrentHashMap<>();

    /** Returns the seconds until the oldest request in the window expires, or 60 if none. */
    private long retryAfterSeconds(String ip) {
        Deque<Long> timestamps = windowMap.get(ip);
        if (timestamps == null) return 60;
        synchronized (timestamps) {
            if (timestamps.isEmpty()) return 60;
            long oldest = timestamps.peekFirst();
            long expiresAt = oldest + WINDOW_MS;
            long remaining = expiresAt - System.currentTimeMillis();
            return Math.max(1, (remaining + 999) / 1000); // ceil to whole seconds
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !("POST".equals(request.getMethod()) &&
                request.getRequestURI().startsWith("/api/scans"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String ip = resolveClientIp(request);
        if (!allow(ip)) {
            long retryAfter = retryAfterSeconds(ip);
            response.setStatus(429);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setHeader("Retry-After", String.valueOf(retryAfter));
            response.getWriter().write(
                    "{\"status\":429,\"error\":\"Too Many Requests\"," +
                    "\"message\":\"Upload limit exceeded. Try again in " + retryAfter + " second(s).\"}");
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean allow(String ip) {
        long now = System.currentTimeMillis();
        long windowStart = now - WINDOW_MS;
        Deque<Long> timestamps = windowMap.computeIfAbsent(ip, k -> new ArrayDeque<>());
        synchronized (timestamps) {
            while (!timestamps.isEmpty() && timestamps.peekFirst() < windowStart) {
                timestamps.pollFirst();
            }
            if (timestamps.size() >= MAX_UPLOADS_PER_MINUTE) return false;
            timestamps.addLast(now);
            return true;
        }
    }

    private static String resolveClientIp(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("X-Forwarded-For"))
                .map(h -> h.split(",")[0].trim())
                .orElse(request.getRemoteAddr());
    }
}
