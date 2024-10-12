package com.recipe.jamanchu.model.dto.request.recipe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.recipe.jamanchu.annotation.AtLeastOneNotEmpty;
import com.recipe.jamanchu.model.type.CookingTimeType;
import com.recipe.jamanchu.model.type.LevelType;
import java.util.List;
import lombok.Getter;

@Getter
@AtLeastOneNotEmpty(message = "하나 이상의 검색 조건을 입력해야 합니다.")
public class RecipesSearchDTO {

  @JsonProperty("ingredientName")
  private final List<String> ingredients;

  @JsonProperty("recipeLevel")
  private final LevelType recipeLevel;

  @JsonProperty("recipeCookingTime")
  private final CookingTimeType recipeCookingTime;

  @JsonCreator
  public RecipesSearchDTO(List<String> ingredients, LevelType recipeLevel, CookingTimeType recipeCookingTime) {
    this.ingredients = ingredients;
    this.recipeLevel = recipeLevel;
    this.recipeCookingTime = recipeCookingTime;
  }
}
