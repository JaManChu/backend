package com.recipe.jamanchu.auth.jwt;

import com.recipe.jamanchu.auth.service.CustomUserDetailService;
import com.recipe.jamanchu.model.type.UserRole;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  private final SecretKey secretKey;
  public static final long ACCESS_TOKEN_EXPIRE_TIME = 10 * 60 * 1000L;  // 10분
  public static final long REFRESH_TOKEN_EXPIRE_TIME = 24 * 60 * 60 * 1000L;  // 24시간
  private final CustomUserDetailService userDetailService;

  public JwtUtil(@Value("${spring.jwt.secret.key}")String secret,
      CustomUserDetailService userDetailService) {

    this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
        Jwts.SIG.HS256.key().build().getAlgorithm());

    this.userDetailService = userDetailService;
  }

  // userId 추출
  public Long getUserId(String token) {
    if(token.startsWith("Bearer ")) {
      token = token.substring(7);
    }
    return Jwts.parser().verifyWith(secretKey).build()
        .parseSignedClaims(token)
        .getPayload()
        .get("userId", Long.class);
  }

  // role 추출
  public String getRole(String token) {
    return Jwts.parser().verifyWith(secretKey).build()
        .parseSignedClaims(token)
        .getPayload()
        .get("role", String.class);
  }

  // UserDetails 조회 및 Authentication 객체 생성
  public Authentication getAuthentication(String userId) {
    UserDetails userDetails = userDetailService.loadUserByUsername(userId);

    return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
  }

  // 토큰 만료기간 검증
  public boolean isExpired(String token) {
    try{
      Jwts.parser().verifyWith(secretKey).build()
          .parseSignedClaims(token)
          .getPayload()
          .getExpiration();

      return false;
    }catch (Exception e) {
      return true;
    }
  }

  // 토큰 생성
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


