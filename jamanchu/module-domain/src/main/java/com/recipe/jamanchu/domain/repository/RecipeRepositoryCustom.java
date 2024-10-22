package com.recipe.jamanchu.repository;

import com.recipe.jamanchu.entity.RecipeEntity;
import com.recipe.jamanchu.model.type.CookingTimeType;
import com.recipe.jamanchu.model.type.LevelType;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecipeRepositoryCustom {
  Page<RecipeEntity> searchAndRecipesQueryDSL(LevelType level, CookingTimeType cookingTime,
      List<String> ingredients, List<Long> scrapedRecipeIds,
      Pageable pageable);
}
