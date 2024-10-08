package com.recipe.jamanchu.model.dto.request.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class UserUpdateDTO {

  @NotEmpty(message = "닉네임을 입력해주세요.")
  @JsonProperty("nickname")
  private String nickname;

  @NotEmpty(message = "기존 비밀번호를 입력해주세요.")
  @JsonProperty("beforePassword")
  private String beforePassword;

  @NotEmpty(message = "변경할 비밀번호를 입력해주세요.")
  @JsonProperty("afterPassword")
  private String afterPassword;

  @JsonCreator
  public UserUpdateDTO(String nickname, String beforePassword, String afterPassword) {
    this.nickname = nickname;
    this.beforePassword = beforePassword;
    this.afterPassword = afterPassword;
  }
}