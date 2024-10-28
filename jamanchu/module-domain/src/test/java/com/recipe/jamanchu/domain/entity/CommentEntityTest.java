package com.recipe.jamanchu.domain.entity;

import static com.recipe.jamanchu.domain.model.type.UserRole.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.domain.repository.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentEntityTest {

  @Mock
  private CommentRepository commentRepository;

  @Test
  @DisplayName("Comment Entity Builder Test")
  void builder() {

    // given
    UserEntity commentUser = UserEntity.builder()
        .usrId(1L)
        .usrNickname("comment")
        .usrEmail("comment@gmail.com")
        .usrPassword("1234")
        .usrRole(USER)
        .build();

    RecipeEntity commentRecipe = RecipeEntity.builder()
        .rcpId(1L)
        .build();

    CommentEntity commentEntity = CommentEntity.builder()
        .cmtId(1L)
        .user(commentUser)
        .recipe(commentRecipe)
        .cmtContent("comment")
        .cmtLike(1.0)
        .build();

    // when
    when(commentRepository.save(commentEntity)).thenReturn(commentEntity);

    // act
    CommentEntity savedComment = commentRepository.save(commentEntity);

    // then
    assertEquals(1, savedComment.getCmtId());
    assertEquals(commentUser.getUsrId(), savedComment.getUser().getUsrId());
    assertEquals(commentRecipe.getRcpId(), savedComment.getRecipe().getRcpId());
    assertEquals("comment", savedComment.getCmtContent());
    assertEquals(1.0, savedComment.getCmtLike());

  }
}