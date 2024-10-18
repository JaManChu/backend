package com.recipe.jamanchu.model.dto.request.comments;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CommentsDTO {

  @Min(value = 1, message = "잘못된 레시피 ID 입력입니다.")
  @JsonProperty("recipeId")
  private final Long recipeId;

  @NotNull(message = "댓글을 입력해주세요.")
  @Size(min = 1, max = 300, message = "댓글은 1자 이상 300자 이하로 입력해주세요.")
  @JsonProperty("comment")
  private final String comment;

  @DecimalMin(value = "1.0", message = "평점은 1.0 이상으로 입력해주세요.")
  @DecimalMax(value = "5.0", message = "평점은 5.0 이하로 입력해주세요.")
  @JsonProperty("rating")
  private final Double rating;

  @JsonCreator
  public CommentsDTO(Long recipeId, String comment, Double rating) {
    this.recipeId = recipeId;
    this.comment = comment;
    this.rating = rating;
  }
}