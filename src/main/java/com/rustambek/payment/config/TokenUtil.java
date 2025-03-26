package com.rustambek.payment.config;

import io.jsonwebtoken.Claims;

public interface TokenUtil {

    String getUsernameFromToken(String token);

    Boolean validateToken(String token);

    Long getAccessExpirationTimeIn();
    Long getRefreshExpirationTimeIn();

    Claims getClaims(String token);

    String generateAccessToken(UserDetailsImpl userDetails);

    String generateRefreshToken(UserDetailsImpl userDetails);
}
