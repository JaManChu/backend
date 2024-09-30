package com.recipe.jamanchu.model.dto.response.recipes;

import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 레시피 조리 순서 List
 */
@Getter
@AllArgsConstructor
public class RecipesManuals {

  @Size(min = 1, message = "조리 순서는 최소 1개 이상이어야 합니다.")
  private final List<RecipesManual> recipesManuals;

}
