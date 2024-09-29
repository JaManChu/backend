package com.recipe.jamanchu.model.dto.request.recipe;

import com.recipe.jamanchu.model.dto.response.ingredients.Ingredients;
import com.recipe.jamanchu.model.dto.response.recipes.RecipesOrders;
import com.recipe.jamanchu.model.type.CookingTimeType;
import com.recipe.jamanchu.model.type.LevelType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class RecipesDTO {

  private final String recipeName;

  private final LevelType level;

  private final CookingTimeType cookingTime;

  private final MultipartFile recipeImage;

  private final Ingredients ingredients;

  private final RecipesOrders orders;

}
