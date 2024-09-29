package com.recipe.jamanchu.model.dto.request.recipe;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecipesDeleteDTO {

  @Min(1)
  private final Long recipeId;
}
