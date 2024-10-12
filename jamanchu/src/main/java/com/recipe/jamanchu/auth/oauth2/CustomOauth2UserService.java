package com.recipe.jamanchu.auth.oauth2;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOauth2UserService  {

  @Value("${PROD.AUTH.OAUTH.REGISTRATION.KAKAO.client-id}")
  private String clientId;

  @Value("${PROD.AUTH.OAUTH.REGISTRATION.KAKAO.client-secret}")
  private String clientSecretId;

  @Value("${PROD.AUTH.OAUTH.REGISTRATION.KAKAO.redirect-uri}")
  private String redirectUri;

  public String getAccessToken(String code) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

    // HTTP Body 생성
    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("grant_type", "authorization_code");
    body.add("client_id", clientId);
    body.add("client_secret", clientSecretId);
    body.add("redirect_uri", redirectUri);
    body.add("code", code);

    // HTTP 요청 보내기
    HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
    RestTemplate rt = new RestTemplate();
    ResponseEntity<Map> response = rt.exchange(
        "https://kauth.kakao.com/oauth/token",
        HttpMethod.POST,
        kakaoTokenRequest,
        Map.class
    );

    // Access-token 반환
    if (response.getStatusCode() == HttpStatus.OK) {
      Map responseBody = response.getBody();
      if (responseBody != null && responseBody.containsKey("access_token")) {
        return responseBody.get("access_token").toString();
      }
    }
    throw new RuntimeException("액세스 토큰을 가져오는 데 실패했습니다.");
  }

  // 사용자 정보 가지고 오기
  public KakaoUserDetails getUserDetails(String accessToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + accessToken);
    headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

    // HTTP 요청 보내기
    HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
    RestTemplate rt = new RestTemplate();
    ResponseEntity<Map> response = rt.exchange(
        "https://kapi.kakao.com/v2/user/me",
        HttpMethod.POST,
        kakaoUserInfoRequest,
        Map.class
    );

    if (response.getStatusCode() == HttpStatus.OK) {
      if (response.getBody() != null) {
        return new KakaoUserDetails(response.getBody());
      }
    }
    throw new RuntimeException("사용자 정보를 가져오는 데 실패했습니다.");
  }
}

