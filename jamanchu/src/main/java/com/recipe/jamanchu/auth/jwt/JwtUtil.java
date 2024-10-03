package com.recipe.jamanchu.auth.jwt;

import com.recipe.jamanchu.model.type.UserRole;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  private final SecretKey secretKey;
  public static final long ACCESS_TOKEN_EXPIRE_TIME = 10 * 60 * 1000L;  // 10분
  public static final long REFRESH_TOKEN_EXPIRE_TIME = 24 * 60 * 60 * 1000L;  // 24시간

  public JwtUtil(@Value("${spring.jwt.secret.key}")String secret) {

    secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
        Jwts.SIG.HS256.key().build().getAlgorithm());
  }

  public Long getUserId(String token) {
    return Jwts.parser().verifyWith(secretKey).build()
        .parseSignedClaims(token)
        .getPayload()
        .get("userId", Long.class);
  }

  public String getRole(String token) {
    return Jwts.parser().verifyWith(secretKey).build()
        .parseSignedClaims(token)
        .getPayload()
        .get("role", String.class);
  }

  public String getType(String token) {
    return Jwts.parser().verifyWith(secretKey).build()
        .parseSignedClaims(token)
        .getPayload()
        .get("type", String.class);
  }

  public void isExpired(String token) {
    Jwts.parser().verifyWith(secretKey).build()
        .parseSignedClaims(token)
        .getPayload()
        .getExpiration();
  }


  public String createJwt(String type, Long userId, UserRole role) {
    long expirationTime = "access".equals(type)
        ? ACCESS_TOKEN_EXPIRE_TIME
        : REFRESH_TOKEN_EXPIRE_TIME;

    return Jwts.builder()
        .claim("type", type)
        .claim("userId", userId)
        .claim("role", role.name())
        .notBefore(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + expirationTime))
        .signWith(secretKey)
        .compact();
  }
}


