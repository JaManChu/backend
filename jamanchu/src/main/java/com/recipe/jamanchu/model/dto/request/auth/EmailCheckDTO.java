package com.recipe.jamanchu.model.dto.request.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class EmailCheckDTO {

  @NotEmpty(message = "이메일을 입력해주세요.")
  @Email(message = "이메일 형식이 아닙니다.")
  @JsonProperty("checkingEmail")
  private final String checkingEmail;

  @JsonCreator
  public EmailCheckDTO(String checkingEmail) {
    this.checkingEmail = checkingEmail;
  }
}
