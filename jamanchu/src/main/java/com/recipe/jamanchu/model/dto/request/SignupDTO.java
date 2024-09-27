package com.recipe.jamanchu.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SignupDTO {

  private String email;
  private String password;
  private String nickname;

}
