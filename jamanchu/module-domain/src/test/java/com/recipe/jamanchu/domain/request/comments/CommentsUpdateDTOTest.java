package com.recipe.jamanchu.domain.request.comments;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.recipe.jamanchu.domain.model.dto.request.comments.CommentsUpdateDTO;
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
class CommentsUpdateDTOTest {

  private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
  private final Validator validator = factory.getValidator();

  @DisplayName("코멘트 ID는 반드시 1이상 이여야 한다.")
  @Test
  void commentIdMustBiggerThan1() {
    //given
    CommentsUpdateDTO commentsUpdateDTO = new CommentsUpdateDTO(0L, "test", 2.0);
    //when
    Set<ConstraintViolation<CommentsUpdateDTO>> violations = validator.validate(commentsUpdateDTO);
    //then
    assertFalse(violations.isEmpty());
    assertEquals("잘못된 댓글 번호입니다.", violations.iterator().next().getMessage());
  }

  @DisplayName("코멘트는 1자 이상 300자 이하로 입력해야 한다.")
  @Test
  void commentMustBetween1And300() {
    //given
    CommentsUpdateDTO commentsUpdateDTO = new CommentsUpdateDTO(1L, "", 2.0);
    //when
    Set<ConstraintViolation<CommentsUpdateDTO>> violations = validator.validate(commentsUpdateDTO);
    //then
    assertFalse(violations.isEmpty());
    assertEquals("댓글 내용은 1자 이상 300자 이하로 입력해주세요.", violations.iterator().next().getMessage());
  }

  @DisplayName("댓글내용은 null이 아니어야 한다.")
  @Test
  void commentMustNotNull() {
    //given
    CommentsUpdateDTO commentsUpdateDTO = new CommentsUpdateDTO(1L, null, 2.0);
    //when
    Set<ConstraintViolation<CommentsUpdateDTO>> violations = validator.validate(commentsUpdateDTO);
    //then
    assertFalse(violations.isEmpty());
    assertEquals("댓글 내용을 입력해주세요.", violations.iterator().next().getMessage());
  }

  @DisplayName("평점은 1.0 이상으로 입력해야 한다.")
  @Test
  void ratingMustBiggerThan1() {
    //given
    CommentsUpdateDTO commentsUpdateDTO = new CommentsUpdateDTO(1L, "test", 0.0);
    //when
    Set<ConstraintViolation<CommentsUpdateDTO>> violations = validator.validate(commentsUpdateDTO);
    //then
    assertFalse(violations.isEmpty());
    assertEquals("평점은 1.0이상으로 입력해주세요.", violations.iterator().next().getMessage());
  }

  @DisplayName("평점은 5.0 미만으로 입력해야 한다.")
  @Test
  void ratingMustSmallerThan5() {
    //given
    CommentsUpdateDTO commentsUpdateDTO = new CommentsUpdateDTO(1L, "test", 6.0);
    //when
    Set<ConstraintViolation<CommentsUpdateDTO>> violations = validator.validate(commentsUpdateDTO);
    //then
    assertFalse(violations.isEmpty());
    assertEquals("평점은 5.0이하로 입력해주세요.", violations.iterator().next().getMessage());
  }
}