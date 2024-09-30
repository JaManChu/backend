package com.recipe.jamanchu.model.dto.request.recipe;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecipesDeleteDTO {

  @Min(value = 1, message = "잘못된 레시피 번호입니다.")
  private final Long recipeId;
}
