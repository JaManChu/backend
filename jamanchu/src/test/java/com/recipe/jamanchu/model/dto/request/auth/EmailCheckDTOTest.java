package com.recipe.jamanchu.model.dto.request.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmailCheckDTOTest {

  private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
  private final Validator validator = factory.getValidator();

  @DisplayName("이메일은 반드시 있어야한다")
  @Test
  void EmailIsNotEmpty() {
    //given
    EmailCheckDTO emailCheckDTO = new EmailCheckDTO("");
    //when
    Set<ConstraintViolation<EmailCheckDTO>> violations = validator.validate(emailCheckDTO);
    //then
    assertEquals(violations.size(), 1);
    assertFalse(violations.isEmpty());
    assertEquals("이메일을 입력해주세요.",violations.iterator().next().getMessage());
  }

  @DisplayName("반드시 이메일 형식이여야한다.")
  @Test
  void MustMatchEmailPattern() {
    //given
    EmailCheckDTO emailCheckDTO = new EmailCheckDTO("test");
    //when
    Set<ConstraintViolation<EmailCheckDTO>> violations = validator.validate(emailCheckDTO);
    //then
    assertEquals(violations.size(), 1);
    assertFalse(violations.isEmpty());
    assertEquals("이메일 형식이 아닙니다.", violations.iterator().next().getMessage());
  }
}