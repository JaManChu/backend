package com.recipe.jamanchu.controller;

import com.recipe.jamanchu.model.dto.request.recipe.RecipesDTO;
import com.recipe.jamanchu.model.dto.request.recipe.RecipesDeleteDTO;
import com.recipe.jamanchu.model.dto.response.ResultResponse;
import com.recipe.jamanchu.service.RecipeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recipes")
public class RecipeController {

  private final RecipeService recipeService;

  @PostMapping
  public ResponseEntity<ResultResponse> registerRecipe(
      HttpServletRequest request,
      @RequestBody RecipesDTO recipesDTO) {
    return ResponseEntity.ok(recipeService.registerRecipe(request, recipesDTO));
  }

  @DeleteMapping
  public ResponseEntity<ResultResponse> deleteRecipe(
      HttpServletRequest request,
      @RequestBody RecipesDeleteDTO recipesDeleteDTO) {
    return ResponseEntity.ok(recipeService.deleteRecipe(request, recipesDeleteDTO));
  }
}
