package com.ndd.simi_be.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ndd.simi_be.common.exception.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class AiRateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_MINUTE = 10;
    private static final Duration WINDOW = Duration.ofMinutes(1);

    private final ObjectMapper objectMapper;

    private final Cache<String, AtomicInteger> requestCounters = Caffeine.newBuilder()
            .maximumSize(100_000)
            .expireAfterWrite(WINDOW)
            .build();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String aiChatPath = request.getContextPath() + "/ai/chat";

        return !"POST".equalsIgnoreCase(request.getMethod())
                || !aiChatPath.equals(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String clientIp = request.getRemoteAddr();

        int requestCount = requestCounters
                .get(clientIp, ignored -> new AtomicInteger())
                .incrementAndGet();

        if (requestCount > MAX_REQUESTS_PER_MINUTE) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setHeader(HttpHeaders.RETRY_AFTER, String.valueOf(WINDOW.toSeconds()));
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            ErrorResponse error = ErrorResponse.builder()
                    .status(HttpStatus.TOO_MANY_REQUESTS.value())
                    .message("Bạn gửi tin nhắn quá nhanh. Vui lòng thử lại sau 1 phút nhé!")
                    .path(request.getRequestURI())
                    .timestamp(LocalDateTime.now())
                    .build();

            objectMapper.writeValue(response.getWriter(), error);
            return;
        }

        filterChain.doFilter(request, response);
    }
}