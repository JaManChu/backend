package com.recipe.jamanchu.model.dto.response.recipes;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 단일 레시피 조리 순서
 */
@Getter
@AllArgsConstructor
public class RecipesManual {

  @NotEmpty(message = "조리 과정을 설명해주세요.")
  private final String recipeOrderContent;

  private final String recipeOrderImage;

}
