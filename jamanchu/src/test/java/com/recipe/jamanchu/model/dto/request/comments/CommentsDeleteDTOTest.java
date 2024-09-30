package com.recipe.jamanchu.model.dto.request.comments;

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
class CommentsDeleteDTOTest {

  private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
  private final Validator validator = factory.getValidator();

  @DisplayName("코멘트 ID는 반드시 1이상 이여야 한다.")
  @Test
  void commentIdMustBiggerThan1() {
    //given
    CommentsDeleteDTO commentsDeleteDTO = new CommentsDeleteDTO(0L);
    //when
    Set<ConstraintViolation<CommentsDeleteDTO>> violations = validator.validate(commentsDeleteDTO);
    //then
    // 예외가 발생하는 것이 있고,
    assertFalse(violations.isEmpty());
    // 그 예외가 무엇인지 확인한다.
    assertEquals("잘못된 입력입니다.", violations.iterator().next().getMessage());
  }
}