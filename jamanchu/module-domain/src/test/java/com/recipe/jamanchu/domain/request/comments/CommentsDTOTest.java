package com.recipe.jamanchu.domain.request.comments;

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
class CommentsDTOTest {

  private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
  private final Validator validator = factory.getValidator();

  @DisplayName("코멘트 ID는 반드시 1이상 이여야 한다.")
  @Test
  void commentIdMustBiggerThan1() {
    //given
    CommentsDTO commentsDTO = new CommentsDTO(0L,"test",2.0);
    //when
    Set<ConstraintViolation<CommentsDTO>> violations = validator.validate(commentsDTO);
    //then
    assertFalse(violations.isEmpty());
    assertEquals("잘못된 레시피 ID 입력입니다.", violations.iterator().next().getMessage());
  }

  @DisplayName("코멘트는 1자 이상 300자 이하로 입력해야 한다.")
  @Test
  void commentMustBetween1And300() {
    //given
    CommentsDTO commentsDTO = new CommentsDTO(1L,"",2.0);
    //when
    Set<ConstraintViolation<CommentsDTO>> violations = validator.validate(commentsDTO);
    //then
    assertFalse(violations.isEmpty());
    assertEquals("댓글은 1자 이상 300자 이하로 입력해주세요.", violations.iterator().next().getMessage());
  }

  @DisplayName("댓글 내용은 null이 아니어야한다.")
  @Test
  void commentMustNotNull() {
    //given
    CommentsDTO commentsDTO = new CommentsDTO(1L,null,2.0);
    //when
    Set<ConstraintViolation<CommentsDTO>> violations = validator.validate(commentsDTO);
    //then
    assertFalse(violations.isEmpty());
    assertEquals("댓글을 입력해주세요.", violations.iterator().next().getMessage());
  }

  @DisplayName("평점은 1.0 이상으로 입력해야 한다.")
  @Test
  void ratingMustBiggerThan1() {
    //given
    CommentsDTO commentsDTO1 = new CommentsDTO(1L,"test",0.0);
    CommentsDTO commentsDTO2 = new CommentsDTO(1L,"test",6.0);
    //when
    Set<ConstraintViolation<CommentsDTO>> violations1 = validator.validate(commentsDTO1);
    Set<ConstraintViolation<CommentsDTO>> violations2 = validator.validate(commentsDTO2);
    //then
    assertFalse(violations1.isEmpty());
    assertFalse(violations2.isEmpty());
    assertEquals("평점은 1.0 이상으로 입력해주세요.", violations1.iterator().next().getMessage());
    assertEquals("평점은 5.0 이하로 입력해주세요.", violations2.iterator().next().getMessage());
  }
}