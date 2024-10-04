package com.recipe.jamanchu.service.impl;

import com.recipe.jamanchu.entity.IngredientEntity;
import com.recipe.jamanchu.entity.ManualEntity;
import com.recipe.jamanchu.entity.RecipeEntity;
import com.recipe.jamanchu.entity.RecipeRatingEntity;
import com.recipe.jamanchu.entity.TenThousandRecipeEntity;
import com.recipe.jamanchu.entity.UserEntity;
import com.recipe.jamanchu.exceptions.exception.UserNotFoundException;
import com.recipe.jamanchu.model.type.RecipeProvider;
import com.recipe.jamanchu.model.type.UserRole;
import com.recipe.jamanchu.repository.IngredientRepository;
import com.recipe.jamanchu.repository.ManualRepository;
import com.recipe.jamanchu.repository.RecipeRatingRepository;
import com.recipe.jamanchu.repository.RecipeRepository;
import com.recipe.jamanchu.repository.TenThousandRecipeRepository;
import com.recipe.jamanchu.repository.UserRepository;
import com.recipe.jamanchu.service.RecipeDivideService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecipeDivideServiceImpl implements RecipeDivideService {

  private final UserRepository userRepository;
  private final RecipeRepository recipeRepository;
  private final RecipeRatingRepository recipeRatingRepository;
  private final ManualRepository manualRepository;
  private final IngredientRepository ingredientRepository;
  private final TenThousandRecipeRepository tenThousandRecipeRepository;

  @Override
  @Transactional
  public void processAndSaveAllData(Long startId, Long endId) {
    List<TenThousandRecipeEntity> scrapedRecipes = tenThousandRecipeRepository.findByCrawledRecipeIdBetween(startId, endId);

    // 데이터가 없으면 종료
    if (scrapedRecipes.isEmpty()) {
      return;
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
      if (recipe != null) {
        saveRecipeRatingData(recipe, scrapedRecipe);  // 평점 저장
        saveManualData(recipe, scrapedRecipe);  // 메뉴얼 저장
        saveIngredientDetails(recipe, scrapedRecipe);  // 재료 상세 저장
      }
    }
  }

  public RecipeEntity saveRecipeData(TenThousandRecipeEntity scrapedRecipe) {
    // 임의의 관리자 이메일을 가져오도록 설정
    UserEntity user = userRepository.findByEmail("user@example.com")
        .orElseThrow(UserNotFoundException::new);

    RecipeEntity recipe = RecipeEntity.builder()
        .user(user)
        .name(scrapedRecipe.getName())
        .level(scrapedRecipe.getLevelType())
        .time(scrapedRecipe.getCookingTimeType())
        .thumbnail(scrapedRecipe.getThumbnail())
        .provider(RecipeProvider.SCRAP)
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

      manualRepository.save(manual);
    }
  }

  public void saveIngredientDetails(RecipeEntity recipe, TenThousandRecipeEntity scrapedRecipe) {
    String[] ingredients = scrapedRecipe.getIngredients().split(",");  // 재료 분리 로직
    for (String ingredient : ingredients) {
      if (ingredient.trim().isEmpty()) {
        continue;  // 비어있는 재료는 무시
      }

      String[] parts = ingredient.split(" ");  // 재료명과 수량 분리
      String name = parts[0];
      String quantity = "";

      // 수량이 있을 경우에만 parts[1]에 접근
      if (parts.length > 1) {
        quantity = parts[1];
      }

      IngredientEntity ingredientEntity = IngredientEntity.builder()
          .recipe(recipe)
          .name(name)
          .quantity(quantity)
          .build();

      ingredientRepository.save(ingredientEntity);
    }
  }
}
