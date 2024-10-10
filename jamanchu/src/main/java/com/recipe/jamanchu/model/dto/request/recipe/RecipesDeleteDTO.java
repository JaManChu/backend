package com.recipe.jamanchu.model.dto.request.recipe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.Getter;

@Getter
public class RecipesDeleteDTO {

  @Min(value = 1, message = "잘못된 레시피 번호입니다.")
  @JsonProperty("recipeId")
  private final Long recipeId;

  @JsonCreator
  public RecipesDeleteDTO(Long recipeId) {
    this.recipeId = recipeId;
  }
}
