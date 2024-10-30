package com.recipe.jamanchu.batch.component.recipe;

import com.recipe.jamanchu.domain.entity.*;
import com.recipe.jamanchu.domain.repository.*;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeDivide {

  private final RecipeRepository recipeRepository;
  private final RecipeRatingRepository recipeRatingRepository;
  private final ManualRepository manualRepository;
  private final IngredientRepository ingredientRepository;
  private final RecipeIngredientRepository recipeIngredientRepository;
  private final RecipeIngredientMappingRepository recipeIngredientMappingRepository;
  private final SeasoningRepository seasoningRepository;

  public List<TenThousandRecipeEntity> processRecipes(List<TenThousandRecipeEntity> tenThousandRecipes) {

    if (tenThousandRecipes.isEmpty()) {
      log.info(">>> 처리할 레시피가 없습니다.");
      return new ArrayList<>();
    }

    List<TenThousandRecipeEntity> validRecipes = new ArrayList<>();
    for (TenThousandRecipeEntity recipe : tenThousandRecipes) {
      if (isRecipeValid(recipe)) {
        validRecipes.add(recipe);
      }
    }

    return validRecipes;
  }

  private boolean isRecipeValid(TenThousandRecipeEntity recipe) {
    return !recipe.getTrMnContents().isEmpty() &&
        !recipe.getTrIngredients().isEmpty() &&
        recipe.getTrMnContents().split("\\$%\\^").length == recipe.getTrMnPictures().split(",").length;
  }

  public void saveRecipeData(RecipeEntity recipe) {
    recipeRepository.save(recipe);
  }

  public void saveRecipeRatingData(RecipeEntity recipe, TenThousandRecipeEntity scrapedRecipe) {
    RecipeRatingEntity rating = RecipeRatingEntity.builder()
        .recipe(recipe)
        .user(recipe.getUser())
        .rrRating(scrapedRecipe.getTrRating())
        .build();

    recipeRatingRepository.save(rating);
  }

  public void saveManualData(RecipeEntity recipe, TenThousandRecipeEntity scrapedRecipe) {
    String[] contents = scrapedRecipe.getTrMnContents().split("\\$%\\^");
    String[] pictures = scrapedRecipe.getTrMnPictures().split(",");
    List<ManualEntity> manualEntities = new ArrayList<>();
    for (int i = 0; i < contents.length; i++) {
      ManualEntity manual = ManualEntity.builder()
          .recipe(recipe)
          .mnContent(contents[i])
          .mnPicture(i < pictures.length ? pictures[i] : "")
          .build();
      manualEntities.add(manual);
    }
    manualRepository.saveAll(manualEntities);
  }

  public void saveIngredientDetails(RecipeEntity recipe, TenThousandRecipeEntity scrapedRecipe) {
    String[] scrapIngredients = scrapedRecipe.getTrIngredients().split(",");  // 재료 분리 로직
    List<RecipeIngredientEntity> recipeIngredientEntities = new ArrayList<>();
    List<RecipeIngredientMappingEntity> recipeIngredientMappingEntities = new ArrayList<>();

    List<String> seasoningNames = seasoningRepository.findAll().stream()
        .map(SeasoningEntity::getSsName)
        .toList();

    for (String recipeIngredient : scrapIngredients) {
      recipeIngredient = recipeIngredient.trim();
      if (recipeIngredient.isEmpty()) {
        continue;  // 비어있는 재료는 무시
      }

      String[] parts = recipeIngredient.split(" ");  // 재료명과 수량 분리
      String name = String.join(" ", Arrays.copyOf(parts, Math.max(parts.length - 1, 1))).trim();
      String quantity = (parts.length > 1) ? parts[parts.length - 1] : "";

      RecipeIngredientEntity recipeIngredientEntity = RecipeIngredientEntity.builder()
          .recipe(recipe)
          .riName(name)
          .riQuantity(quantity)
          .build();

      recipeIngredientEntities.add(recipeIngredientEntity);

      boolean isSeasoning = seasoningNames.stream().anyMatch(recipeIngredient::contains);
      if (isSeasoning) {
        continue;  // 재료명이 양념류를 포함하면 Ingredient entity 에는 저장하지 않고 건너뜀
      }

      // 데이터베이스에서 해당 이름의 IngredientEntity가 존재하는지 확인
      IngredientEntity ingredient = ingredientRepository.findByIngName(name)
          .orElseGet(() -> {
            // 없으면 새로운 IngredientEntity 생성
            IngredientEntity newIngredient = IngredientEntity.builder().ingName(name).build();
            return ingredientRepository.save(newIngredient); // 새 엔티티 저장
          });

      RecipeIngredientMappingEntity recipeIngredientMappingEntity = RecipeIngredientMappingEntity.builder()
          .recipe(recipe)
          .ingredient(ingredient)
          .build();

      recipeIngredientMappingEntities.add(recipeIngredientMappingEntity);
    }
    recipeIngredientRepository.saveAll(recipeIngredientEntities);
    recipeIngredientMappingRepository.saveAll(recipeIngredientMappingEntities);
  }
}