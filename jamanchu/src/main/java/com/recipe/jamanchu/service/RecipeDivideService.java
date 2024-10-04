package com.recipe.jamanchu.service;

import com.recipe.jamanchu.entity.RecipeEntity;
import com.recipe.jamanchu.entity.TenThousandRecipeEntity;
import com.recipe.jamanchu.model.dto.response.ResultResponse;

public interface RecipeDivideService {
  ResultResponse processAndSaveAllData(Long startId, Long endId);
}
