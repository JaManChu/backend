package com.recipe.jamanchu.api.service;

import com.recipe.jamanchu.domain.model.dto.request.comments.CommentsDTO;
import com.recipe.jamanchu.domain.model.dto.request.comments.CommentsDeleteDTO;
import com.recipe.jamanchu.domain.model.dto.request.comments.CommentsUpdateDTO;
import com.recipe.jamanchu.domain.model.dto.response.ResultResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface CommentsService {

  // 댓글 작성 API
  ResultResponse writeComment(HttpServletRequest request, CommentsDTO commentsDTO);

  // 댓글 수정 API
  ResultResponse updateComment(HttpServletRequest request, CommentsUpdateDTO commentsUpdateDTO);

  // 댓글 삭제 API
  ResultResponse deleteComment(HttpServletRequest request, CommentsDeleteDTO commentsDeleteDTO);

  // 특정 레시피의 댓글 목록 조회 API
  ResultResponse getCommentsList(Long recipeId);
}
