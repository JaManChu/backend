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

  // 레시피 수정 API
  ResultResponse updateRecipe(HttpServletRequest request, RecipesUpdateDTO recipesUpdateDTO);

  // 모든 레시피 조회 API
  ResultResponse getRecipes(HttpServletRequest request, int page, int size);

  // 특정 조건 레시피 조회 API
  ResultResponse searchRecipes(HttpServletRequest request, RecipesSearchDTO recipesSearchDTO, int page, int size);

  // 레시피 상세 페이지 조회 API
  ResultResponse getRecipeDetail(Long recipesId);

  // 인기 레시피 리스트 API
  ResultResponse getRecipesByRating(HttpServletRequest request, int page, int size);

  // 레시피 스크랩 API
  ResultResponse scrapedRecipe(HttpServletRequest request, Long recipeId);

  // 레시피 삭제 API
  ResultResponse deleteRecipe(HttpServletRequest request, RecipesDeleteDTO recipesDeleteDTO);
}
