package com.ecommerce.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${jwt.header}")
    private String authorizationHeader;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long validityInMilliseconds;

    public String createToken(String email, String role) {
        return Jwts.builder()
                .claim("role", role)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + validityInMilliseconds))
                .signWith(getSignInKey())
                .compact();

    }

        public String resolveToken(ServerHttpRequest request) {
        String headerAuth = request.getHeaders().getFirst(authorizationHeader);
        if (headerAuth == null || !headerAuth.startsWith("Bearer ")) {
            throw new JwtException("Invalid JWT token");
        }else {
            return headerAuth.substring(7);
        }
    }

    public String validateToken(String token) {
        Claims jwsClaims = getJwsClaims(token);
        if(jwsClaims.getExpiration().before(new Date())){
            throw new JwtException("Expired or invalid JWT token");
        }else{
            return jwsClaims.getSubject();
        }
    }

    public String parseToken(String token) {
        return getJwsClaims(token).getSubject();
    }

    private Claims getJwsClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

}
