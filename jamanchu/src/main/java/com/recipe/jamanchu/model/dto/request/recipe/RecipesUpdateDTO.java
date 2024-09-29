package com.recipe.jamanchu.model.dto.request.recipe;

import com.recipe.jamanchu.model.dto.response.ingredients.Ingredients;
import com.recipe.jamanchu.model.dto.response.recipes.RecipesOrders;
import com.recipe.jamanchu.model.type.CookingTimeType;
import com.recipe.jamanchu.model.type.LevelType;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class RecipesUpdateDTO extends RecipesDTO{

  @Min(1)
  private final Long recipeId;

  public RecipesUpdateDTO(
      String recipeName,
      LevelType level,
      CookingTimeType cookingTime,
      MultipartFile recipeImage,
      Ingredients ingredients,
      RecipesOrders orders,
      Long recipeId) {
    super(recipeName, level, cookingTime, recipeImage, ingredients, orders);
    this.recipeId = recipeId;
  }
}
