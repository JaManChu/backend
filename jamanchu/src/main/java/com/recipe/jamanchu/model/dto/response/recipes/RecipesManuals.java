package com.recipe.jamanchu.model.dto.response.recipes;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 레시피 조리 순서 List
 */
@Getter
@AllArgsConstructor
public class RecipesManuals {

  private final List<RecipesManual> recipesManuals;

}
