package com.recipe.jamanchu.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.component.UserAccessHandler;
import com.recipe.jamanchu.entity.IngredientEntity;
import com.recipe.jamanchu.entity.ManualEntity;
import com.recipe.jamanchu.entity.RecipeEntity;
import com.recipe.jamanchu.entity.RecipeRatingEntity;
import com.recipe.jamanchu.entity.TenThousandRecipeEntity;
import com.recipe.jamanchu.entity.UserEntity;
import com.recipe.jamanchu.model.dto.response.ResultResponse;
import com.recipe.jamanchu.model.type.CookingTimeType;
import com.recipe.jamanchu.model.type.LevelType;
import com.recipe.jamanchu.model.type.RecipeProvider;
import com.recipe.jamanchu.model.type.UserRole;
import com.recipe.jamanchu.repository.IngredientRepository;
import com.recipe.jamanchu.repository.ManualRepository;
import com.recipe.jamanchu.repository.RecipeRatingRepository;
import com.recipe.jamanchu.repository.RecipeRepository;
import com.recipe.jamanchu.repository.TenThousandRecipeRepository;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class RecipeDivideServiceImplTest {
  @Mock
  private UserAccessHandler userAccessHandler;

  @Mock
  private RecipeRepository recipeRepository;

  @Mock
  private RecipeRatingRepository recipeRatingRepository;

  @Mock
  private ManualRepository manualRepository;

  @Mock
  private IngredientRepository ingredientRepository;

  @Mock
  private TenThousandRecipeRepository tenThousandRecipeRepository;

  @InjectMocks
  private RecipeDivideServiceImpl recipeDivideService;

  private TenThousandRecipeEntity scrapRecipe;
  private UserEntity user;

  @BeforeEach
  void setUp() {
    scrapRecipe = TenThousandRecipeEntity.builder()
        .name("Sample Recipe")
        .levelType(LevelType.LOW)
        .cookingTimeType(CookingTimeType.TEN_MINUTES)
        .thumbnail("thumbnail.jpg")
        .crManualContents("Step 1$%^Step 2")
        .crManualPictures("pic1.jpg,pic2.jpg")
        .ingredients("ingredient1 100g,ingredient2 200ml")
        .rating(4.5)
        .build();

    user = UserEntity.builder()
        .userId(1L)
        .email("user@example.com")
        .password("password")
        .nickname("nickname")
        .provider("local")
        .providerId("provider_id")
        .role(UserRole.USER)
        .build();
  }

  @Test
  void testProcessAndSaveAllData() {
    // given
    when(userAccessHandler.findByEmail("user@example.com")).thenReturn(user);

    List<TenThousandRecipeEntity> recipeList = Collections.singletonList(scrapRecipe);

    when(tenThousandRecipeRepository.findByCrawledRecipeIdBetween(1L, 1L)).thenReturn(recipeList);

    RecipeEntity mockRecipe = RecipeEntity.builder()
        .user(user)
        .name("Sample Recipe")
        .level(LevelType.LOW)
        .time(CookingTimeType.TEN_MINUTES)
        .thumbnail("thumbnail.jpg")
        .provider(RecipeProvider.SCRAP)
        .build();
    when(recipeRepository.save(any(RecipeEntity.class))).thenReturn(mockRecipe);

    // when
    ResultResponse resultResponse = recipeDivideService.processAndSaveAllData(1L, 1L);

    // then
    assertEquals("데이터 분산 저장 성공!", resultResponse.getMessage());

    // verify
    verify(recipeRepository, times(1)).save(any(RecipeEntity.class));
    verify(recipeRatingRepository, times(1)).save(any(RecipeRatingEntity.class));
    verify(manualRepository, times(2)).save(any(ManualEntity.class)); // 2개의 매뉴얼 단계가 있음
    verify(ingredientRepository, times(2)).save(any(IngredientEntity.class)); // 2개의 재료가 있음
  }

  @Test
  void testSaveRecipeData() {
    // given
    when(userAccessHandler.findByEmail("user@example.com")).thenReturn(user);

    RecipeEntity recipe = RecipeEntity.builder()
        .user(user)
        .name(scrapRecipe.getName())
        .level(scrapRecipe.getLevelType())
        .time(scrapRecipe.getCookingTimeType())
        .thumbnail(scrapRecipe.getThumbnail())
        .provider(RecipeProvider.SCRAP)
        .build();
    when(recipeRepository.save(any(RecipeEntity.class))).thenReturn(recipe);

    // when
    RecipeEntity savedRecipe = recipeDivideService.saveRecipeData(scrapRecipe);

    // then
    assertEquals(1L, savedRecipe.getUser().getUserId());
    assertEquals("Sample Recipe", savedRecipe.getName());
    assertEquals(LevelType.LOW, savedRecipe.getLevel());
    assertEquals(CookingTimeType.TEN_MINUTES, savedRecipe.getTime());
    assertEquals("thumbnail.jpg", savedRecipe.getThumbnail());
    assertEquals(RecipeProvider.SCRAP, savedRecipe.getProvider());

    // 결과 검증
    verify(recipeRepository, times(1)).save(any(RecipeEntity.class));
  }

  @Test
  void testSaveRecipeRatingData() {
    // given
    RecipeEntity recipe = RecipeEntity.builder().build();

    // when
    recipeDivideService.saveRecipeRatingData(recipe, scrapRecipe);

    // then
    assertEquals(4.5, scrapRecipe.getRating());

    // verify
    verify(recipeRatingRepository, times(1)).save(any(RecipeRatingEntity.class));
  }

  @Test
  void testSaveManualData() {
    // given
    RecipeEntity recipe = RecipeEntity.builder().build();

    // when
    recipeDivideService.saveManualData(recipe, scrapRecipe);

    // then
    assertEquals("Step 1$%^Step 2", scrapRecipe.getCrManualContents());
    assertEquals("pic1.jpg,pic2.jpg", scrapRecipe.getCrManualPictures());

    // verify
    verify(manualRepository, times(2)).save(any(ManualEntity.class));
  }

  @Test
  void testSaveIngredientDetails() {
    // given
    RecipeEntity recipe = RecipeEntity.builder().build();

    // when
    recipeDivideService.saveIngredientDetails(recipe, scrapRecipe);

    // then
    assertEquals("ingredient1 100g,ingredient2 200ml", scrapRecipe.getIngredients());

    // verify
    verify(ingredientRepository, times(2)).save(any(IngredientEntity.class));
  }

}