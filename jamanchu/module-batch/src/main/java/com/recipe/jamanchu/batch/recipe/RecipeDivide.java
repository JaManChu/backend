package com.recipe.jamanchu.batch.recipe;

import com.recipe.jamanchu.domain.component.UserAccessHandler;
import com.recipe.jamanchu.domain.entity.IngredientEntity;
import com.recipe.jamanchu.domain.entity.ManualEntity;
import com.recipe.jamanchu.domain.entity.RecipeEntity;
import com.recipe.jamanchu.domain.entity.RecipeIngredientEntity;
import com.recipe.jamanchu.domain.entity.RecipeIngredientMappingEntity;
import com.recipe.jamanchu.domain.entity.RecipeRatingEntity;
import com.recipe.jamanchu.domain.entity.SeasoningEntity;
import com.recipe.jamanchu.domain.entity.TenThousandRecipeEntity;
import com.recipe.jamanchu.domain.entity.UserEntity;
import com.recipe.jamanchu.domain.model.type.RecipeProvider;
import com.recipe.jamanchu.domain.repository.IngredientRepository;
import com.recipe.jamanchu.domain.repository.ManualRepository;
import com.recipe.jamanchu.domain.repository.RecipeIngredientMappingRepository;
import com.recipe.jamanchu.domain.repository.RecipeIngredientRepository;
import com.recipe.jamanchu.domain.repository.RecipeRatingRepository;
import com.recipe.jamanchu.domain.repository.RecipeRepository;
import com.recipe.jamanchu.domain.repository.SeasoningRepository;
import com.recipe.jamanchu.domain.repository.TenThousandRecipeRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeDivide {

  private final UserAccessHandler userAccessHandler;
  private final RecipeRepository recipeRepository;
  private final RecipeRatingRepository recipeRatingRepository;
  private final ManualRepository manualRepository;
  private final IngredientRepository ingredientRepository;
  private final RecipeIngredientRepository recipeIngredientRepository;
  private final RecipeIngredientMappingRepository recipeIngredientMappingRepository;
  private final TenThousandRecipeRepository tenThousandRecipeRepository;
  private final SeasoningRepository seasoningRepository;

  public void processAndSaveAllData(Long startId, Long endId) {
    List<TenThousandRecipeEntity> scrapedRecipes = tenThousandRecipeRepository.findByTrOriginIdBetween(startId, endId);

    // 데이터가 없으면 종료
    if (scrapedRecipes.isEmpty()) {
      return;
    }

    // 각 레시피 데이터를 처리
    for (TenThousandRecipeEntity scrapedRecipe : scrapedRecipes) {
      String[] contents = scrapedRecipe.getTrMnContents().split("\\$%\\^");
      String[] pictures = scrapedRecipe.getTrMnPictures().split(",");
      // 메뉴얼이 없는 경우 건너뜀
      if (scrapedRecipe.getTrMnContents().isEmpty()) continue;
      // 재료가 없는 경우 건너뜀
      if (scrapedRecipe.getTrIngredients().isEmpty()) continue;
      // 조리 순서와 조리 순서 사진의 갯수가 같지 않은 경우 건너뜀
      if (contents.length != pictures.length) continue;

      RecipeEntity recipe = saveRecipeData(scrapedRecipe);  // 레시피 저장
      saveRecipeRatingData(recipe, scrapedRecipe);  // 평점 저장
      saveManualData(recipe, scrapedRecipe);  // 메뉴얼 저장
      saveIngredientDetails(recipe, scrapedRecipe);  // 재료 상세 저장
    }

    log.info("각 테이블에 맞게 데이터 저장 성공");
  }

  public void weeklyRecipeDivide() {
    Long lastOriginRecipeId = recipeRepository.findMaxRcpOriginId();
    Long lastScrapRecipeId = tenThousandRecipeRepository.findMaxTrOriginId();
    processAndSaveAllData(lastOriginRecipeId + 1, lastScrapRecipeId);
  }

  public RecipeEntity saveRecipeData(TenThousandRecipeEntity scrapedRecipe) {
    // 임의의 관리자 이메일을 가져오도록 설정
    UserEntity user = userAccessHandler.findByEmail("user@example.com");

    RecipeEntity recipe = RecipeEntity.builder()
        .user(user)
        .rcpName(scrapedRecipe.getTrName())
        .rcpLevel(scrapedRecipe.getTrLevel())
        .rcpTime(scrapedRecipe.getTrCookTime())
        .rcpThumbnail(scrapedRecipe.getTrThumbnail())
        .rcpProvider(RecipeProvider.SCRAP)
        .rcpOriginId(scrapedRecipe.getTrOriginId())
        .build();

    recipeRepository.save(recipe);
    return recipe;
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
    String[] pictures;
    List<ManualEntity> manualEntities = new ArrayList<>();
    if(scrapedRecipe.getTrMnPictures() == null || scrapedRecipe.getTrMnPictures().isEmpty()) {
      pictures = new String[contents.length];
    } else {
      pictures = scrapedRecipe.getTrMnPictures().split(",");
    }
    for (int i = 0; i < contents.length; i++) {
      ManualEntity manual = ManualEntity.builder()
          .recipe(recipe)
          .mnContent(contents[i])
          .mnPicture(pictures[i] != null ? pictures[i] : "")
          .build();

      manualEntities.add(manual);
    }
    manualRepository.saveAll(manualEntities);
  }

  public void saveIngredientDetails(RecipeEntity recipe, TenThousandRecipeEntity scrapedRecipe) {
    String[] scrapIngredients = scrapedRecipe.getTrIngredients().split(",");  // 재료 분리 로직
    List<RecipeIngredientEntity> recipeIngredientEntities = new ArrayList<>();
    List<IngredientEntity> ingredientEntities = new ArrayList<>();
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
      StringBuilder sb = new StringBuilder();
      String quantity = "";
      if (parts.length == 1) {
        sb.append(parts[0]);
      } else {
        for (int i = 0; i < parts.length - 1; i++) {
          sb.append(parts[i]).append(" ");
        }

        quantity = parts[parts.length - 1];
      }
      String name = sb.toString().trim();

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

      Optional<IngredientEntity> ingredientEntity = ingredientRepository.findByIngName(name);
      IngredientEntity ingredient;
      if (ingredientEntity.isPresent()) {
        ingredient = ingredientEntity.get();
      } else {
        ingredient = IngredientEntity.builder()
            .ingName(name)
            .build();
        ingredientEntities.add(ingredient);
      }

      RecipeIngredientMappingEntity recipeIngredientMappingEntity = RecipeIngredientMappingEntity.builder()
          .recipe(recipe)
          .ingredient(ingredient)
          .build();

      recipeIngredientMappingEntities.add(recipeIngredientMappingEntity);
    }
    ingredientRepository.saveAll(ingredientEntities);
    recipeIngredientRepository.saveAll(recipeIngredientEntities);
    recipeIngredientMappingRepository.saveAll(recipeIngredientMappingEntities);
  }
}
