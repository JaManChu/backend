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
class PasswordCheckDTOTest {

  private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
  private final Validator validator = factory.getValidator();

  @DisplayName("비밀번호는 반드시 있어야한다")
  @Test
  void EmailIsNotEmpty() {
    //given
    PasswordCheckDTO passwordCheckDTO = new PasswordCheckDTO("");
    //when
    Set<ConstraintViolation<PasswordCheckDTO>> violations = validator.validate(passwordCheckDTO);
    //then
    assertEquals(violations.size(), 1);
    assertFalse(violations.isEmpty());
    assertEquals("비밀번호를 입력해주세요.",violations.iterator().next().getMessage());
  }
}