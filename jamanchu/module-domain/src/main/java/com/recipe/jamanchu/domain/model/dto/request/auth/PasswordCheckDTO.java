package com.recipe.jamanchu.domain.model.dto.request.auth;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class PasswordCheckDTO {

  @NotEmpty(message = "비밀번호를 입력해주세요.")
  @JsonProperty("password")
  private String password;

  @JsonCreator
  public PasswordCheckDTO(String password) {
    this.password = password;
  }
}
