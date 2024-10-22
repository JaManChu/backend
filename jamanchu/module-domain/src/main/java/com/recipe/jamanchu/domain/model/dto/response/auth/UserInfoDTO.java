package com.recipe.jamanchu.domain.model.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoDTO {
  private String email;
  private String nickname;
}
