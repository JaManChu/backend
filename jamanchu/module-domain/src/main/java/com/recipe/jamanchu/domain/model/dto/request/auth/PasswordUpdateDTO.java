package com.recipe.jamanchu.domain.model.dto.request.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PasswordUpdateDTO {

  @NotNull(message = "회원 ID가 없습니다.")
  @JsonProperty("userId")
  private Long userId;

  @JsonProperty("password")
  private String password;

  @JsonCreator
  public PasswordUpdateDTO(Long userId, String password) {
    this.userId = userId;
    this.password = password;
  }
}
