package com.recipe.jamanchu.api.auth.oauth2;

import java.util.Map;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class KakaoUserDetails {

  private Map<String, Object> attributes;

  public String getProviderId() {
    return attributes.get("id").toString();
  }

  public String getEmail() {
    return (String) ((Map<?, ?>) attributes.get("kakao_account")).get("email");
  }

  public String getNickname() {
    return (String) ((Map<?, ?>) attributes.get("properties")).get("nickname");
  }
}
