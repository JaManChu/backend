package com.recipe.jamanchu.domain.model.dto.request.recipe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.recipe.jamanchu.domain.model.dto.response.ingredients.Ingredient;
import com.recipe.jamanchu.domain.model.dto.response.recipes.RecipesManual;
import com.recipe.jamanchu.domain.model.type.CookingTimeType;
import com.recipe.jamanchu.domain.model.type.LevelType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;

@Getter
public class RecipesUpdateDTO extends RecipesDTO {

  @Min(value= 1, message = "레시피 아이디는 1 이상이어야 합니다.")
  @NotNull(message = "레시피 아이디가 없습니다.")
  @JsonProperty("recipeId")
  private final Long recipeId;

  @JsonCreator
  public RecipesUpdateDTO(
      String recipeName,
      LevelType level,
      CookingTimeType cookingTime,
      String recipeThumbnail,
      List<Ingredient> ingredients,
      List<RecipesManual> manuals,
      Long recipeId) {
    super(recipeName, level, cookingTime, recipeThumbnail, ingredients, manuals);
    this.recipeId = recipeId;
  }
}
