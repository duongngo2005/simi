package com.ndd.simi_be.auth.service;

import com.ndd.simi_be.auth.dto.request.LoginRequest;
import com.ndd.simi_be.auth.dto.request.RegisterRequest;
import com.ndd.simi_be.auth.dto.response.AuthTokenResponse;
import com.ndd.simi_be.auth.refresh.RefreshToken;
import com.ndd.simi_be.auth.refresh.RefreshTokenRepository;
import com.ndd.simi_be.common.exception.ConflictException;
import com.ndd.simi_be.common.exception.InvalidTokenException;
import com.ndd.simi_be.config.JwtConfig;
import com.ndd.simi_be.security.JwtService;
import com.ndd.simi_be.user.entity.User;
import com.ndd.simi_be.user.mapper.UserMapper;
import com.ndd.simi_be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder encoder;
    private final JwtConfig jwtConfig;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public AuthTokenResponse register(RegisterRequest request){
        if (userRepository.existsByEmail(request.getEmail())){
            throw new ConflictException("Email đã được sử dụng");
        }

        User user = User.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .passwordHash(encoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .build();

        user = userRepository.save(user);

        return createAuthTokenResponse(user);
    }

    @Transactional
    public AuthTokenResponse login(LoginRequest request){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = (User) authentication.getPrincipal();

        return createAuthTokenResponse(user);
    }

    public AuthTokenResponse refresh(String refreshTokenStr){
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(refreshTokenStr)
                .orElseThrow(() -> new InvalidTokenException("Refresh token không hợp lệ"));

        if (refreshToken.isRevoked()){
            throw new InvalidTokenException("Token đã bị thu hồi");
        }

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new InvalidTokenException("Token đã hết hạn");
        }

        User user = refreshToken.getUser();

        String newAccessToken = jwtService.generateToken(user);

        return AuthTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshTokenStr)
                .userResponse(UserMapper.toUserResponse(user))
                .build();
    }

    @Transactional
    public void logout(String refreshTokenStr){
        refreshTokenRepository.findByRefreshToken(refreshTokenStr)
                .ifPresent(token -> {
                    token.setRevoked(true);
                });
    }

    private AuthTokenResponse createAuthTokenResponse(User user){
        String accessToken = jwtService.generateToken(user);
        String refreshTokenStr = jwtService.generateRefresh(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken(refreshTokenStr)
                .expiresAt(LocalDateTime.now().plus(Duration.ofMillis(jwtConfig.getRefreshExpirationMs())))
                .user(user)
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);

        return AuthTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr)
                .userResponse(UserMapper.toUserResponse(user))
                .build();
    }
}
