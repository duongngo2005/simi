package com.ndd.simi_be.security;

import com.ndd.simi_be.config.JwtConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtConfig jwtConfig;

    public String generateToken(UserDetails userDetails){
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", userDetails.getAuthorities().iterator().next().getAuthority());
        return buildToken(extraClaims, userDetails.getUsername(), jwtConfig.getAccessExpirationMs());
    }

    public String generateRefresh(UserDetails userDetails){
        return buildToken(new HashMap<>(), userDetails.getUsername(), jwtConfig.getRefreshExpirationMs());
    }

    private String buildToken(Map<String, Object> extraClaims, String subject, long expirationMs){
        return Jwts.builder()
                .claims()
                .add(extraClaims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .and()
                .signWith(getSigningKey())
                .compact();
    }

    public String extractEmail(String token){
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public Date extractExpiration(String token){
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getExpiration();
    }

    public boolean isTokenValid(UserDetails userDetails, String token){
        return extractEmail(token).equalsIgnoreCase(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    private SecretKey getSigningKey(){
        byte[] keyBytes = Decoders.BASE64URL.decode(jwtConfig.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
