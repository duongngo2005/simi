package com.ndd.simi_be.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndd.simi_be.common.exception.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain
    ) throws ServletException, IOException
    {
        String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        try{
            String userEmail = jwtService.extractEmail(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                if (jwtService.isTokenValid(userDetails, jwt)){
                    UsernamePasswordAuthenticationToken authenticationToken
                            = new UsernamePasswordAuthenticationToken(
                                    userDetails,
                            null,
                                    userDetails.getAuthorities()
                    );

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }catch (UsernameNotFoundException ex){
            log.warn(ex.getMessage());
            sendErrorResponse(request, response, ex.getMessage());
            return;
        }
        catch (ExpiredJwtException ex){
            log.warn("Token đã hết hạn");
            sendErrorResponse(request, response, "Token đã hết hạn");
            return;
        }
        catch (JwtException ex){
            sendErrorResponse(request, response, "Token không hợp lệ");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(
            HttpServletRequest request,
            HttpServletResponse response,
            String message
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                objectMapper.writeValueAsString(
                        ErrorResponse.builder()
                                .status(401)
                                .message(message)
                                .path(request.getRequestURI())
                                .timestamp(LocalDateTime.now())
                                .build()
                )
        );
    }
}
