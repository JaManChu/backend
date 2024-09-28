package com.recipe.jamanchu.entity;

import static com.recipe.jamanchu.model.type.UserRole.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.repository.CommentRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@ExtendWith(MockitoExtension.class)
class CommentEntityTest {

  @Mock
  private CommentRepository commentRepository;

  @Test
  @DisplayName("Comment Entity Builder Test")
  void builder() {

    // given
    UserEntity commentUser = UserEntity.builder()
        .userId(1L)
        .nickname("comment")
        .email("comment@gmail.com")
        .password("1234")
        .role(USER)
        .build();

    RecipeEntity commentRecipe = RecipeEntity.builder()
        .id(1L)
        .build();

    CommentEntity commentEntity = CommentEntity.builder()
        .commentId(1L)
        .user(commentUser)
        .recipe(commentRecipe)
        .commentContent("comment")
        .commentLike(1.0)
        .build();

    // when
    when(commentRepository.save(commentEntity)).thenReturn(commentEntity);

    // act
    CommentEntity savedComment = commentRepository.save(commentEntity);

    // then
    assertEquals(1, savedComment.getCommentId());
    assertEquals(commentUser.getUserId(), savedComment.getUser().getUserId());
    assertEquals(commentRecipe.getId(), savedComment.getRecipe().getId());
    assertEquals("comment", savedComment.getCommentContent());
    assertEquals(1.0, savedComment.getCommentLike());

  }
}