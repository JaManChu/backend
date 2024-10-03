package com.recipe.jamanchu.model.dto.request.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserUpdateDTO {

  private Long userId;
  private String password;
  private String nickname;

}
