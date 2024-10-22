package com.recipe.jamanchu.domain.model.dto.request.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class UserUpdateDTO {

  @NotEmpty(message = "닉네임을 입력해주세요.")
  @JsonProperty("nickname")
  private String nickname;

  @NotEmpty(message = "변경할 비밀번호를 입력해주세요.")
  @JsonProperty("password")
  private String password;

  @JsonCreator
  public UserUpdateDTO(String nickname, String password) {
    this.nickname = nickname;
    this.password = password;
  }
}