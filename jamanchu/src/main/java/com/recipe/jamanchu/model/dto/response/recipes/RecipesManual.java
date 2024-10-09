package com.recipe.jamanchu.model.dto.response.recipes;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 단일 레시피 조리 순서
 */
@Getter
@AllArgsConstructor
public class RecipesManual {

  @Min(value = 1, message = "조리 순서는 1이상이어야 합니다.")
  private final Long recipeOrder;

  @NotEmpty(message = "조리 과정을 설명해주세요.")
  private final String recipeOrderContent;

  private final String recipeOrderImage;

}
