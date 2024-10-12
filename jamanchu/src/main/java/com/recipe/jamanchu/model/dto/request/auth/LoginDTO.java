package com.recipe.jamanchu.model.dto.request.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class LoginDTO {

  @NotEmpty(message = "이메일을 입력해주세요.")
  @Email(message = "이메일 형식이 아닙니다.")
  @JsonProperty("email")
  private String email;

  @NotEmpty(message = "비밀번호를 입력해주세요.")
  @JsonProperty("password")
  private String password;

  @JsonCreator
  public LoginDTO(String email, String password) {
    this.email = email;
    this.password = password;
  }
}
