package com.recipe.jamanchu.domain.model.dto.request.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class PasswordUpdateDTO {

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
