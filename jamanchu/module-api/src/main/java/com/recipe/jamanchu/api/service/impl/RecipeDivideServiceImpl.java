package com.recipe.jamanchu.api.service.impl;

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
import com.recipe.jamanchu.domain.model.dto.response.ResultResponse;
import com.recipe.jamanchu.domain.model.type.RecipeProvider;
import com.recipe.jamanchu.domain.model.type.ResultCode;
import com.recipe.jamanchu.domain.repository.IngredientRepository;
import com.recipe.jamanchu.domain.repository.ManualRepository;
import com.recipe.jamanchu.domain.repository.RecipeIngredientMappingRepository;
import com.recipe.jamanchu.domain.repository.RecipeIngredientRepository;
import com.recipe.jamanchu.domain.repository.RecipeRatingRepository;
import com.recipe.jamanchu.domain.repository.RecipeRepository;
import com.recipe.jamanchu.domain.repository.SeasoningRepository;
import com.recipe.jamanchu.domain.repository.TenThousandRecipeRepository;
import com.recipe.jamanchu.api.service.RecipeDivideService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecipeDivideServiceImpl implements RecipeDivideService {

  private final UserAccessHandler userAccessHandler;
  private final RecipeRepository recipeRepository;
  private final RecipeRatingRepository recipeRatingRepository;
  private final ManualRepository manualRepository;
  private final IngredientRepository ingredientRepository;
  private final RecipeIngredientRepository recipeIngredientRepository;
  private final RecipeIngredientMappingRepository recipeIngredientMappingRepository;
  private final TenThousandRecipeRepository tenThousandRecipeRepository;
  private final SeasoningRepository seasoningRepository;

  @Override
  public ResultResponse processAndSaveAllData(Long startId, Long endId) {
    List<TenThousandRecipeEntity> scrapedRecipes = tenThousandRecipeRepository.findByRecipeIdBetween(startId, endId);

    // 데이터가 없으면 종료
    if (scrapedRecipes.isEmpty()) {
      return null;
    }

    // 각 레시피 데이터를 처리
    for (TenThousandRecipeEntity scrapedRecipe : scrapedRecipes) {
      String[] contents = scrapedRecipe.getCrManualContents().split("\\$%\\^");
      String[] pictures = scrapedRecipe.getCrManualPictures().split(",");
      // 메뉴얼이 없는 경우 건너뜀
      if (scrapedRecipe.getCrManualContents().isEmpty()) continue;
      // 재료가 없는 경우 건너뜀
      if (scrapedRecipe.getIngredients().isEmpty()) continue;
      // 조리 순서와 조리 순서 사진의 갯수가 같지 않은 경우 건너뜀
      if (contents.length != pictures.length) continue;

      RecipeEntity recipe = saveRecipeData(scrapedRecipe);  // 레시피 저장
      saveRecipeRatingData(recipe, scrapedRecipe);  // 평점 저장
      saveManualData(recipe, scrapedRecipe);  // 메뉴얼 저장
      saveIngredientDetails(recipe, scrapedRecipe);  // 재료 상세 저장
    }

    return ResultResponse.of(ResultCode.SUCCESS_INSERT_CR_DATA);
  }

  @Scheduled(cron = "0 30 0 * * SUN")
  public void weeklyRecipeDivide() {
    Long lastOriginRecipeId = recipeRepository.findMaxOriginRcpId();
    Long lastScrapRecipeId = tenThousandRecipeRepository.findMaxRecipeId();
    processAndSaveAllData(lastOriginRecipeId + 1, lastScrapRecipeId);
  }

  public RecipeEntity saveRecipeData(TenThousandRecipeEntity scrapedRecipe) {
    // 임의의 관리자 이메일을 가져오도록 설정
    UserEntity user = userAccessHandler.findByEmail("user@example.com");

    RecipeEntity recipe = RecipeEntity.builder()
        .user(user)
        .name(scrapedRecipe.getName())
        .level(scrapedRecipe.getLevelType())
        .time(scrapedRecipe.getCookingTimeType())
        .thumbnail(scrapedRecipe.getThumbnail())
        .provider(RecipeProvider.SCRAP)
        .originRcpId(scrapedRecipe.getRecipeId())
        .build();

    recipeRepository.save(recipe);
    return recipe;
  }

  public void saveRecipeRatingData(RecipeEntity recipe, TenThousandRecipeEntity scrapedRecipe) {
    RecipeRatingEntity rating = RecipeRatingEntity.builder()
        .recipe(recipe)
        .user(recipe.getUser())
        .rating(scrapedRecipe.getRating())
        .build();

    recipeRatingRepository.save(rating);
  }

  public void saveManualData(RecipeEntity recipe, TenThousandRecipeEntity scrapedRecipe) {
    String[] contents = scrapedRecipe.getCrManualContents().split("\\$%\\^");
    String[] pictures;
    List<ManualEntity> manualEntities = new ArrayList<>();
    if(scrapedRecipe.getCrManualPictures() == null || scrapedRecipe.getCrManualPictures().isEmpty()) {
      pictures = new String[contents.length];
    } else {
      pictures = scrapedRecipe.getCrManualPictures().split(",");
    }
    for (int i = 0; i < contents.length; i++) {
      ManualEntity manual = ManualEntity.builder()
          .recipe(recipe)
          .manualContent(contents[i])
          .manualPicture(pictures[i] != null ? pictures[i] : "")
          .build();

      manualEntities.add(manual);
    }
    manualRepository.saveAll(manualEntities);
  }

  public void saveIngredientDetails(RecipeEntity recipe, TenThousandRecipeEntity scrapedRecipe) {
    String[] scrapIngredients = scrapedRecipe.getIngredients().split(",");  // 재료 분리 로직
    List<RecipeIngredientEntity> recipeIngredientEntities = new ArrayList<>();
    List<IngredientEntity> ingredientEntities = new ArrayList<>();
    List<RecipeIngredientMappingEntity> recipeIngredientMappingEntities = new ArrayList<>();

    List<String> seasoningNames = seasoningRepository.findAll().stream()
        .map(SeasoningEntity::getName)
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
          .name(name)
          .quantity(quantity)
          .build();

      recipeIngredientEntities.add(recipeIngredientEntity);

      boolean isSeasoning = seasoningNames.stream().anyMatch(recipeIngredient::contains);
      if (isSeasoning) {
        continue;  // 재료명이 양념류를 포함하면 Ingredient entity 에는 저장하지 않고 건너뜀
      }

      Optional<IngredientEntity> ingredientEntity = ingredientRepository.findByIngredientName(name);
      IngredientEntity ingredient;
      if (ingredientEntity.isPresent()) {
        ingredient = ingredientEntity.get();
      } else {
        ingredient = IngredientEntity.builder()
            .ingredientName(name)
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
