package com.itiutiaiev.chipper.security

import io.jsonwebtoken.Jwts
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import static io.jsonwebtoken.io.Decoders.BASE64URL
import static io.jsonwebtoken.security.Keys.hmacShaKeyFor

import javax.crypto.SecretKey

@Component
class JwtUtil {

    @Value('${jwt.secret.key}')
    private String secretString

    @Value('${spring.application.name}')
    private String issuer

    @Value('${jwt.validity.time}')
    private long validityTime

    private SecretKey secretKey

    @PostConstruct
    void initKey() {
        secretKey = hmacShaKeyFor(BASE64URL.decode(secretString))
    }

    String createToken(String email) {
        Date now = new Date()
        Date validity = new Date(now.time + validityTime)
        Jwts.builder()
                .issuer(issuer)
                .subject(email)
                .issuedAt(now)
                .notBefore(now)
                .expiration(validity)
                .signWith(secretKey)
                .compact()
    }

    String extractEmail(String token) {
        extractAllProperties(token).sub
    }

    boolean isTokenValid(String token) {
        Map payload = extractAllProperties(token)
        isIssuerValid(payload) && !isTokenExpired(payload)
    }

    private static boolean isTokenExpired(Map payload) {
        new Date(payload.exp * 1000).before(new Date())
    }

    private String isIssuerValid(Map payload) {
        payload.iss == issuer
    }

    private Map extractAllProperties(String token) {
        Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parse(token)
                .payload as Map
    }
}