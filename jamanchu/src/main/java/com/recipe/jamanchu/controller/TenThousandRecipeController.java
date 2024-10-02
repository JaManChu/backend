package com.recipe.jamanchu.controller;

import com.recipe.jamanchu.scraper.ScrapTenThousandRecipe;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TenThousandRecipeController {

  private final ScrapTenThousandRecipe recipeScrap;

  @GetMapping("/scrape-recipes")
  public String scrapeRecipes() {
    recipeScrap.scrap();
    return "Scraping completed!";
  }

}
