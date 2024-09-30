package com.recipe.jamanchu.model.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailCheckDTO {

  @NotEmpty(message = "이메일을 입력해주세요.")
  @Email(message = "이메일 형식이 아닙니다.")
  private final String checkingEmail;
}
