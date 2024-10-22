package com.recipe.jamanchu.api.service;

import com.recipe.jamanchu.domain.model.dto.request.recipe.RecipesDTO;
import com.recipe.jamanchu.domain.model.dto.request.recipe.RecipesDeleteDTO;
import com.recipe.jamanchu.domain.model.dto.request.recipe.RecipesSearchDTO;
import com.recipe.jamanchu.domain.model.dto.request.recipe.RecipesUpdateDTO;
import com.recipe.jamanchu.domain.model.dto.response.ResultResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

public interface RecipeService {

  // 레시피 작성 API
  ResultResponse registerRecipe(HttpServletRequest request, RecipesDTO recipesDTO, String thumbnail, List<String> OrderImages);

  // 레시피 수정 API
  ResultResponse updateRecipe(HttpServletRequest request, RecipesUpdateDTO recipesUpdateDTO, String thumbnail, List<String> OrderImages);

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

  // 추천 레시피 조회 API
  ResultResponse getRecommendRecipes(HttpServletRequest request);
}
