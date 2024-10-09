package com.recipe.jamanchu.model.dto.request.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class DeleteUserDTO {

  @NotEmpty(message = "비밀번호를 입력해주세요.")
  @JsonProperty("password")
  private String password;

  @JsonCreator
  public DeleteUserDTO(String password) {
    this.password = password;
  }
}