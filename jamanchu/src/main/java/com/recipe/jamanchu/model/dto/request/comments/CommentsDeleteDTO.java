package com.recipe.jamanchu.model.dto.request.comments;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.Getter;

@Getter
public class CommentsDeleteDTO {

  @Min(value = 1, message = "잘못된 입력입니다.")
  @JsonProperty("commentId")
  private final Long commentId;

  @JsonCreator
  public CommentsDeleteDTO(Long commentId) {
    this.commentId = commentId;
  }
}
