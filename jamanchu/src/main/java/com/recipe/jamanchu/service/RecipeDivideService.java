package com.recipe.jamanchu.service;

import com.recipe.jamanchu.entity.RecipeEntity;
import com.recipe.jamanchu.entity.TenThousandRecipeEntity;

public interface RecipeDivideService {
  void processAndSaveAllData(Long startId, Long endId);
}
