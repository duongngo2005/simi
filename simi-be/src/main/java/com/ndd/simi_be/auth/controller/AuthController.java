package com.ndd.simi_be.auth.controller;

import com.ndd.simi_be.auth.dto.request.LoginRequest;
import com.ndd.simi_be.auth.dto.request.RegisterRequest;
import com.ndd.simi_be.auth.dto.response.AuthTokenResponse;
import com.ndd.simi_be.auth.service.AuthService;
import com.ndd.simi_be.common.exception.InvalidTokenException;
import com.ndd.simi_be.common.response.ApiResponse;
import com.ndd.simi_be.config.JwtConfig;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtConfig jwtConfig;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthTokenResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ){
        AuthTokenResponse authTokenResponse = authService.register(request);

        ApiResponse<AuthTokenResponse> response = ApiResponse.<AuthTokenResponse>builder()
                .status(200)
                .message("Đăng ký thành công")
                .body(authTokenResponse)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, createRefreshCookie(authTokenResponse.getRefreshToken()).toString())
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthTokenResponse>> login(@Valid @RequestBody LoginRequest request){
        AuthTokenResponse authTokenResponse = authService.login(request);

        ApiResponse<AuthTokenResponse> response = ApiResponse.<AuthTokenResponse>builder()
                .message("Đăng nhập thành công")
                .status(200)
                .body(authTokenResponse)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, createRefreshCookie(authTokenResponse.getRefreshToken()).toString())
                .body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthTokenResponse>> refreshToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ){
        if (refreshToken == null || refreshToken.isBlank()){
            throw new InvalidTokenException("Refresh token không hợp lệ hoặc rỗng");
        }

        ApiResponse<AuthTokenResponse> response = ApiResponse.<AuthTokenResponse>builder()
                .body(authService.refresh(refreshToken))
                .status(200)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ){
        authService.logout(refreshToken);
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .maxAge(0)
                .httpOnly(true)
                .secure(false)
                .path("/api/v1/auth")
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    private ResponseCookie createRefreshCookie(String refreshToken){
        return ResponseCookie.from("refreshToken", refreshToken)
                .maxAge(Duration.ofMillis(jwtConfig.getRefreshExpirationMs()))
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/api/v1/auth")
                .build();
    }
}