package com.rustambek.payment.config;

import com.rustambek.payment.exception.TokenExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtUtil implements TokenUtil{
    @Value("${app.jjwt.secret}")
    private String secret;

    @Value("${app.jjwt.access_expiration}")
    private String accessExpirationTime;

    @Value("${app.jjwt.refresh_expiration}")
    private String refreshExpirationTime;

    @Override
    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }
    @Override
    public Claims getClaims(String token) {
        Claims claims;
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
            claims = Jwts
                    .parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            claims = e.getClaims();
        }
        return claims;
    }

    @Override
    public String generateAccessToken(UserDetailsImpl userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userDetails.getUsername());
        return doGenerateToken(claims, userDetails.getUsername(), accessExpirationTime);
    }

    private String doGenerateToken(Map<String, Object> claims, String username, String expirationTime) {
        long expirationTimeLong = Long.parseLong(expirationTime);
        final Date createdDate = new Date();
        final Date expirationDate = new Date(createdDate.getTime() + expirationTimeLong * 1000);
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(key)
                .compact();
    }

    @Override
    public String generateRefreshToken(UserDetailsImpl userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userDetails.getUsername());
        return doGenerateToken(claims, userDetails.getId().toString(), refreshExpirationTime);
    }

    @Override
    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    @Override
    public Long getAccessExpirationTimeIn() {
        return Long.parseLong(this.accessExpirationTime);
    }

    @Override
    public Long getRefreshExpirationTimeIn() {
        return Long.parseLong(this.refreshExpirationTime);
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration;
        try {
            expiration = getExpirationDateFromToken(token);
        } catch (TokenExpiredException e) {
            return true;
        }
        return expiration.before(new Date());
    }
    private Date getExpirationDateFromToken(String token) throws TokenExpiredException {
        return getAllClaimsFromToken(token).getExpiration();
    }
    private Claims getAllClaimsFromToken(String token) throws TokenExpiredException {
        Claims claims;
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
            claims = Jwts
                    .parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException("Token expired");
        }
        return claims;
    }

}
