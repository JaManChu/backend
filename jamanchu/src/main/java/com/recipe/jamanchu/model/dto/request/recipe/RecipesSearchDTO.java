package com.recipe.jamanchu.model.dto.request.recipe;

import com.recipe.jamanchu.model.type.CookingTimeType;
import com.recipe.jamanchu.model.type.LevelType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecipesSearchDTO {

  private final List<String> ingredients;

  private final LevelType level;

  private final CookingTimeType cookingTime;

}
