package com.recipe.jamanchu.api.service.impl;

import static com.recipe.jamanchu.domain.model.type.RecipeProvider.SCRAP;

import com.recipe.jamanchu.api.auth.jwt.JwtUtil;
import com.recipe.jamanchu.api.service.CommentsService;
import com.recipe.jamanchu.api.service.NotifyService;
import com.recipe.jamanchu.core.exceptions.exception.RecipeNotFoundException;
import com.recipe.jamanchu.core.exceptions.exception.UnmatchedUserException;
import com.recipe.jamanchu.domain.component.UserAccessHandler;
import com.recipe.jamanchu.domain.entity.CommentEntity;
import com.recipe.jamanchu.domain.entity.IngredientRatingEntity;
import com.recipe.jamanchu.domain.entity.RecipeEntity;
import com.recipe.jamanchu.domain.entity.RecipeRatingEntity;
import com.recipe.jamanchu.domain.entity.UserEntity;
import com.recipe.jamanchu.domain.model.dto.request.comments.CommentsDTO;
import com.recipe.jamanchu.domain.model.dto.request.comments.CommentsDeleteDTO;
import com.recipe.jamanchu.domain.model.dto.request.comments.CommentsUpdateDTO;
import com.recipe.jamanchu.domain.model.dto.response.ResultResponse;
import com.recipe.jamanchu.domain.model.dto.response.comments.Comment;
import com.recipe.jamanchu.domain.model.dto.response.comments.Comments;
import com.recipe.jamanchu.domain.model.dto.response.notify.Notify;
import com.recipe.jamanchu.domain.model.type.ResultCode;
import com.recipe.jamanchu.domain.model.type.TokenType;
import com.recipe.jamanchu.domain.repository.CommentRepository;
import com.recipe.jamanchu.domain.repository.IngredientRatingRepository;
import com.recipe.jamanchu.domain.repository.RecipeRatingRepository;
import com.recipe.jamanchu.domain.repository.RecipeRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommentsServiceImpl implements CommentsService {

  private final CommentRepository commentRepository;
  private final RecipeRepository recipeRepository;
  private final RecipeRatingRepository recipeRatingRepository;

  private final UserAccessHandler userAccessHandler;

  private final NotifyService notifyService;

  private final JwtUtil jwtUtil;
  private final IngredientRatingRepository ingredientRatingRepository;

  @Transactional(rollbackOn = RuntimeException.class)
  @Override
  public ResultResponse writeComment(HttpServletRequest request, CommentsDTO commentsDTO) {

    // Token 검사
    Long userId = jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue()));

    // 유저 존재 검사
    UserEntity user = userAccessHandler.findByUserId(userId);

    Long recipeId = commentsDTO.getRecipeId();

    // 레시피 존재 검사
    RecipeEntity recipe = recipeRepository.findById(recipeId)
        .orElseThrow(RecipeNotFoundException::new);

    // 댓글 중복 검사
    if(commentRepository.existsByUserAndRecipe(user, recipe)) {
      return ResultResponse.of(ResultCode.FAIL_DUPLICATE_COMMENTS);
    }

    CommentEntity userComment = CommentEntity.builder()
        .user(user)
        .recipe(recipe)
        .cmtContent(commentsDTO.getComment())
        .cmtLike(commentsDTO.getRating())
        .build();

    // 레시피 평가 데이터 저장
    RecipeRatingEntity recipeRating = RecipeRatingEntity.builder()
        .user(user)
        .recipe(recipe)
        .rrRating(commentsDTO.getRating())
        .build();

    recipeRatingRepository.save(recipeRating);

    // 재료 평가 데이터 저장
    recipe.getMapping().forEach(
        ingredient -> {
          IngredientRatingEntity ingredientRating = IngredientRatingEntity.builder()
              .user(user)
              .ingredient(ingredient.getIngredient())
              .irRating(commentsDTO.getRating())
              .irPoint(
                  ingredientRatingRepository.findAllByIngredient(ingredient.getIngredient()).stream().mapToDouble(IngredientRatingEntity::getIrRating).sum() + commentsDTO.getRating() / (ingredientRatingRepository.findAll().size()+1)
              )
              .build();
          ingredientRatingRepository.save(ingredientRating);
        });

    commentRepository.save(userComment);

    //알림 전송 부분
    if(recipe.getRcpProvider() != SCRAP){
      Notify notify = Notify.of(recipeId, recipe.getRcpName(), commentsDTO.getComment(),commentsDTO.getRating(), user.getUsrNickname());
      notifyService.notifyUser(recipe,recipe.getUser().getUsrId(), notify);
    }

    return ResultResponse.of(ResultCode.SUCCESS_COMMENTS);
  }

  @Transactional(rollbackOn = RuntimeException.class)
  @Override
  public ResultResponse updateComment(HttpServletRequest request, CommentsUpdateDTO commentsUpdateDTO) {

    // Token 검사
    Long userId = jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue()));

    // 유저 존재 검사
    UserEntity user = userAccessHandler.findByUserId(userId);

    // 댓글 존재 검사
    Long commentId = commentsUpdateDTO.getCommentsId();

    CommentEntity comment = commentRepository.findById(commentId)
        .orElseThrow(RecipeNotFoundException::new);

    // 유저 일치 검사
    if(!Objects.equals(user.getUsrId(), comment.getUser().getUsrId())) {
      throw new UnmatchedUserException();
    }

    comment.updateComment(commentsUpdateDTO.getComment(), commentsUpdateDTO.getRating());

    return ResultResponse.of(ResultCode.SUCCESS_UPDATE_COMMENTS);
  }

  @Transactional(rollbackOn = RuntimeException.class)
  @Override
  public ResultResponse deleteComment(HttpServletRequest request, CommentsDeleteDTO commentsDeleteDTO) {

    // Token 검사
    Long userId = jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue()));

    // 유저 존재 검사
    UserEntity user = userAccessHandler.findByUserId(userId);

    // 댓글 존재 검사
    Long commentId = commentsDeleteDTO.getCommentId();

    CommentEntity comment = commentRepository.findById(commentId)
        .orElseThrow(RecipeNotFoundException::new);

    // 유저 일치 검사
    if(!Objects.equals(user.getUsrId(), comment.getUser().getUsrId())) {
      throw new UnmatchedUserException();
    }

    commentRepository.deleteById(commentId);

    return ResultResponse.of(ResultCode.SUCCESS_DELETE_COMMENT);
  }

  @Override
  public ResultResponse getCommentsList(Long recipeId) {

    // 레시피 존재 검사
    RecipeEntity recipe = recipeRepository.findById(recipeId)
        .orElseThrow(RecipeNotFoundException::new);

    List<CommentEntity> allByRecipe = commentRepository.findAllByRecipeOrderByCreatedAtAsc(recipe);

    Comments comments = Comments.of(
        allByRecipe.stream()
          .map(Comment::of)
          .collect(Collectors.toList())
    );

    return ResultResponse.of(ResultCode.SUCCESS_RETRIEVE_COMMENTS, comments);
  }
}
