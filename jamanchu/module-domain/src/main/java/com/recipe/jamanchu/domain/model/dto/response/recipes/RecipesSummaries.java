package com.recipe.jamanchu.domain.model.dto.response.recipes;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 레시피 요약 List
 */
@Getter
@AllArgsConstructor
public class RecipesSummaries {

  private final List<RecipesSummary> recipesSummaries;
}
