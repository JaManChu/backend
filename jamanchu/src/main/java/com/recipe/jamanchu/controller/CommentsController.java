package com.recipe.jamanchu.controller;

import com.recipe.jamanchu.model.dto.request.comments.CommentsDTO;
import com.recipe.jamanchu.model.dto.request.comments.CommentsDeleteDTO;
import com.recipe.jamanchu.model.dto.request.comments.CommentsUpdateDTO;
import com.recipe.jamanchu.model.dto.response.ResultResponse;
import com.recipe.jamanchu.service.CommentsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@RestController
public class CommentsController {

  private final CommentsService commentsService;

  @GetMapping("/{recipeId}")
  public ResponseEntity<ResultResponse> getCommentsListByRecipe(
      @PathVariable(value = "recipeId") Long recipeId) {
    return ResponseEntity.ok(commentsService.getCommentsList(recipeId));
  }

  @PostMapping
  public ResponseEntity<ResultResponse> writeComment(
      HttpServletRequest request,
      @Valid @RequestBody CommentsDTO commentsDTO) {
    return ResponseEntity.ok(commentsService.writeComment(request, commentsDTO));
  }

  @PutMapping
  public ResponseEntity<ResultResponse> updateComment(
      HttpServletRequest request,
      @Valid @RequestBody CommentsUpdateDTO commentsUpdateDTO) {
    return ResponseEntity.ok(commentsService.updateComment(request, commentsUpdateDTO));
  }

  @DeleteMapping
  public ResponseEntity<ResultResponse> deleteComment(
      HttpServletRequest request,
      @Valid @RequestBody CommentsDeleteDTO commentsDeleteDTO) {
    return ResponseEntity.ok(commentsService.deleteComment(request, commentsDeleteDTO));
  }
}
