package com.recipe.jamanchu.service;

import com.recipe.jamanchu.model.dto.request.recipe.RecipesDTO;
import com.recipe.jamanchu.model.dto.request.recipe.RecipesDeleteDTO;
import com.recipe.jamanchu.model.dto.request.recipe.RecipesSearchDTO;
import com.recipe.jamanchu.model.dto.request.recipe.RecipesUpdateDTO;
import com.recipe.jamanchu.model.dto.response.ResultResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface RecipeService {

  // 레시피 작성 API
  ResultResponse registerRecipe(HttpServletRequest request, RecipesDTO recipesDTO);

  // 레시피 삭제 API
  ResultResponse deleteRecipe(HttpServletRequest request, RecipesDeleteDTO recipesDeleteDTO);
}
