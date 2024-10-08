package com.recipe.jamanchu.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.auth.jwt.JwtUtil;
import com.recipe.jamanchu.component.UserAccessHandler;
import com.recipe.jamanchu.entity.CommentEntity;
import com.recipe.jamanchu.entity.RecipeEntity;
import com.recipe.jamanchu.entity.UserEntity;
import com.recipe.jamanchu.exceptions.exception.UnmatchedUserException;
import com.recipe.jamanchu.model.dto.request.comments.CommentsDTO;
import com.recipe.jamanchu.model.dto.request.comments.CommentsDeleteDTO;
import com.recipe.jamanchu.model.dto.request.comments.CommentsUpdateDTO;
import com.recipe.jamanchu.model.dto.response.comments.Comments;
import com.recipe.jamanchu.model.type.UserRole;
import com.recipe.jamanchu.repository.CommentRepository;
import com.recipe.jamanchu.repository.RecipeRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentsServiceImplTest {

  @Mock
  private CommentRepository commentRepository;

  @Mock
  private RecipeRepository recipeRepository;

  @Mock
  private UserAccessHandler userAccessHandler;

  @Mock
  private NotifyServiceImpl notifyService;

  @Mock
  private JwtUtil jwtUtil;

  @Mock
  private HttpServletRequest request;

  @InjectMocks
  private CommentsServiceImpl commentService;

  @DisplayName("댓글 작성 테스트")
  @Test
  void writeComment() {

    // given
    Long userId = 1L;

    UserEntity user = UserEntity.builder()
        .userId(userId)
        .nickname("heesang")
        .email("test@gmail.com")
        .password("1234")
        .role(UserRole.USER)
        .provider(null)
        .providerId(null)
        .build();

    Long recipeId = 1L;

    RecipeEntity recipe = RecipeEntity.builder()
        .id(recipeId)
        .user(user)
        .build();
    CommentsDTO requestDTO = new CommentsDTO(recipeId, "댓글 내용", 5.0);

    // when
    when(jwtUtil.getUserId(request.getHeader("access-token"))).thenReturn(userId);
    when(userAccessHandler.findByUserId(userId)).thenReturn(user);
    when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

    // then
    assertEquals("댓글 작성 성공!", commentService.writeComment(request, requestDTO).getMessage());

  }

  @DisplayName("댓글 수정 테스트")
  @Test
  void updateComment() {
    // given
    Long userId = 1L;

    UserEntity user = UserEntity.builder()
        .userId(userId)
        .nickname("heesang")
        .email("test@gmail.com")
        .password("1234")
        .role(UserRole.USER)
        .provider(null)
        .providerId(null)
        .build();

    Long commentId = 1L;

    CommentEntity oldComment = CommentEntity.builder()
        .user(user)
        .recipe(any())
        .commentContent("댓글 내용")
        .commentLike(5.0)
        .build();

    CommentsUpdateDTO requestDTO = new CommentsUpdateDTO(commentId, "새로운 댓글 내용", 4.0);

    // when
    when(jwtUtil.getUserId(request.getHeader("access-token"))).thenReturn(userId);
    when(userAccessHandler.findByUserId(userId)).thenReturn(user);
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(oldComment));

    // then
    assertEquals("댓글 수정 성공!", commentService.updateComment(request, requestDTO).getMessage());
    assertEquals("새로운 댓글 내용", oldComment.getCommentContent());
    assertEquals(4.0, oldComment.getCommentLike());

  }

  @DisplayName("댓글 수정 테스트 - 유저가 달라서 실패하는 경우")
  @Test
  void failToUpdateCommentByUnmatchedUser() {

    // given

    Long commentUserId = 1L;
    UserEntity commentUser = UserEntity.builder()
        .userId(commentUserId)
        .nickname("heesang")
        .email("test@gmail.com")
        .password("1234")
        .role(UserRole.USER)
        .provider(null)
        .providerId(null)
        .build();

    Long requestUserId = 2L;
    UserEntity requestUser = UserEntity.builder()
        .userId(requestUserId)
        .nickname("heesang")
        .email("test@gmail.com")
        .password("1234")
        .role(UserRole.USER)
        .provider(null)
        .providerId(null)
        .build();

    CommentEntity oldComment = CommentEntity.builder()
        .user(commentUser)
        .recipe(any())
        .commentContent("댓글 내용")
        .commentLike(5.0)
        .build();

    Long commentId = 1L;
    CommentsUpdateDTO requestDTO = new CommentsUpdateDTO(commentId, "새로운 댓글 내용", 4.0);

    //when
    when(jwtUtil.getUserId(request.getHeader("access-token"))).thenReturn(requestUserId);
    when(userAccessHandler.findByUserId(requestUserId)).thenReturn(requestUser);
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(oldComment));

    //then
    assertThrows(
        UnmatchedUserException.class, () -> commentService.updateComment(request, requestDTO));
  }

  @DisplayName("댓글 삭제 테스트")
  @Test
  void deleteComment() {
    // given
    Long userId = 1L;

    UserEntity user = UserEntity.builder()
        .userId(userId)
        .nickname("heesang")
        .email("test@gmail.com")
        .build();

    Long commentId = 1L;

    CommentEntity comment = CommentEntity.builder()
        .user(user)
        .recipe(any())
        .commentContent("댓글 내용")
        .commentLike(5.0)
        .build();

    CommentsDeleteDTO requestDTO = new CommentsDeleteDTO(commentId);
    // when
    when(jwtUtil.getUserId(request.getHeader("access-token"))).thenReturn(userId);
    when(userAccessHandler.findByUserId(userId)).thenReturn(user);
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

    // then
    assertEquals("댓글 삭제 성공!", commentService.deleteComment(request, requestDTO).getMessage());
  }

  @DisplayName("레시피의 댓글 조회 테스트")
  @Test
  void getCommentsList() {
    // given
    Long recipeId = 1L;

    RecipeEntity recipe = RecipeEntity.builder()
        .id(recipeId)
        .build();

    List<CommentEntity> allByRecipe = List.of(
        CommentEntity.builder()
            .user(
                UserEntity.builder()
                    .userId(1L)
                    .build()
            )
            .recipe(recipe)
            .commentContent("댓글 내용1")
            .commentLike(5.0)
            .build(),
        CommentEntity.builder()
            .user(
                UserEntity.builder()
                    .userId(1L)
                    .build()
            )
            .recipe(recipe)
            .commentContent("댓글 내용2")
            .commentLike(4.0)
            .build(),
        CommentEntity.builder()
            .user(
                UserEntity.builder()
                    .userId(1L)
                    .build()
            )
            .recipe(recipe)
            .commentContent("댓글 내용3")
            .commentLike(3.0)
            .build(),
        CommentEntity.builder()
            .user(
                UserEntity.builder()
                    .userId(1L)
                    .build()
            )
            .recipe(recipe)
            .commentContent("댓글 내용4")
            .commentLike(2.0)
            .build()
    );

    // when
    when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
    when(commentRepository.findAllByRecipe(recipe)).thenReturn(allByRecipe);

    // then
    Comments data = (Comments) commentService.getCommentsList(recipeId).getData();
    assertEquals(4, data.getComments().size());
    assertEquals("댓글 내용1", data.getComments().get(0).getContent());
    assertEquals(5.0, data.getComments().get(0).getRating());

  }
}