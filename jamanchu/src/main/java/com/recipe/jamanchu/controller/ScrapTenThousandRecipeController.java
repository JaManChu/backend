package com.recipe.jamanchu.controller;

import com.recipe.jamanchu.model.dto.response.ResultResponse;
import com.recipe.jamanchu.scraper.ScrapTenThousandRecipe;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ScrapTenThousandRecipeController {

  private final ScrapTenThousandRecipe recipeScrap;

  @GetMapping("/scrape-recipes")
  public ResponseEntity<ResultResponse> scrapeRecipes(
      @RequestParam Long startRecipeId,
      @RequestParam Long stopRecipeId
  ) {
    recipeScrap.scrap(startRecipeId, stopRecipeId);
    return ResponseEntity.ok(recipeScrap.scrap(startRecipeId, stopRecipeId));
  }

}
