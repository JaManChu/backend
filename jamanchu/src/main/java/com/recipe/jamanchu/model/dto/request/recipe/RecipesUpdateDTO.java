package com.recipe.jamanchu.model.dto.request.recipe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.recipe.jamanchu.model.dto.response.ingredients.Ingredients;
import com.recipe.jamanchu.model.dto.response.recipes.RecipesManuals;
import com.recipe.jamanchu.model.type.CookingTimeType;
import com.recipe.jamanchu.model.type.LevelType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class RecipesUpdateDTO extends RecipesDTO{

  @Min(value= 1, message = "레시피 아이디는 1 이상이어야 합니다.")
  @NotNull(message = "레시피 아이디가 없습니다.")
  @JsonProperty("recipeId")
  private final Long recipeId;

  @JsonCreator
  public RecipesUpdateDTO(
      String recipeName,
      LevelType level,
      CookingTimeType cookingTime,
      MultipartFile recipeImage,
      Ingredients ingredients,
      RecipesManuals orders,
      Long recipeId) {
    super(recipeName, level, cookingTime, recipeImage, ingredients, orders);
    this.recipeId = recipeId;
  }
}
