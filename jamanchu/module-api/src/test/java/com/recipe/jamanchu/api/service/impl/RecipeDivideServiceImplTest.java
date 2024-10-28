package com.recipe.jamanchu.api.service.impl;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.domain.component.UserAccessHandler;
import com.recipe.jamanchu.domain.entity.IngredientEntity;
import com.recipe.jamanchu.domain.entity.ManualEntity;
import com.recipe.jamanchu.domain.entity.RecipeEntity;
import com.recipe.jamanchu.domain.entity.RecipeIngredientEntity;
import com.recipe.jamanchu.domain.entity.RecipeRatingEntity;
import com.recipe.jamanchu.domain.entity.TenThousandRecipeEntity;
import com.recipe.jamanchu.domain.entity.UserEntity;
import com.recipe.jamanchu.domain.model.dto.response.ResultResponse;
import com.recipe.jamanchu.domain.model.type.CookingTimeType;
import com.recipe.jamanchu.domain.model.type.LevelType;
import com.recipe.jamanchu.domain.model.type.RecipeProvider;
import com.recipe.jamanchu.domain.model.type.UserRole;
import com.recipe.jamanchu.domain.repository.IngredientRepository;
import com.recipe.jamanchu.domain.repository.ManualRepository;
import com.recipe.jamanchu.domain.repository.RecipeIngredientMappingRepository;
import com.recipe.jamanchu.domain.repository.RecipeIngredientRepository;
import com.recipe.jamanchu.domain.repository.RecipeRatingRepository;
import com.recipe.jamanchu.domain.repository.RecipeRepository;
import com.recipe.jamanchu.domain.repository.SeasoningRepository;
import com.recipe.jamanchu.domain.repository.TenThousandRecipeRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
  private IngredientRepository ingredientRepository;

  @Mock
  private ManualRepository manualRepository;

  @Mock
  private SeasoningRepository seasoningRepository;

  @Mock
  private RecipeIngredientRepository recipeIngredientRepository;

  @Mock
  private TenThousandRecipeRepository tenThousandRecipeRepository;

  @Mock
  RecipeIngredientMappingRepository recipeIngredientMappingRepository;

  @InjectMocks
  private RecipeDivideServiceImpl recipeDivideService;

  private TenThousandRecipeEntity scrapRecipe;
  private UserEntity user;

  @BeforeEach
  void setUp() {
    scrapRecipe = TenThousandRecipeEntity.builder()
        .trName("Sample Recipe")
        .trLevel(LevelType.LOW)
        .trCookTime(CookingTimeType.TEN_MINUTES)
        .trThumbnail("thumbnail.jpg")
        .trMnContents("Step 1$%^Step 2")
        .trMnPictures("pic1.jpg,pic2.jpg")
        .trIngredients("ingredient1 100g,ingredient2 200ml")
        .trRating(4.5)
        .build();

    user = UserEntity.builder()
        .usrId(1L)
        .usrEmail("user@example.com")
        .usrPassword("password")
        .usrNickname("nickname")
        .usrProvider("local")
        .usrProviderSub("provider_id")
        .usrRole(UserRole.USER)
        .build();
  }

  @Test
  void testProcessAndSaveAllData() {
    // given
    when(userAccessHandler.findByEmail("user@example.com")).thenReturn(user);

    List<TenThousandRecipeEntity> recipeList = Collections.singletonList(scrapRecipe);

    when(tenThousandRecipeRepository.findByTrOriginIdBetween(1L, 1L)).thenReturn(recipeList);

    RecipeEntity mockRecipe = RecipeEntity.builder()
        .user(user)
        .rcpName("Sample Recipe")
        .rcpLevel(LevelType.LOW)
        .rcpTime(CookingTimeType.TEN_MINUTES)
        .rcpThumbnail("thumbnail.jpg")
        .rcpProvider(RecipeProvider.SCRAP)
        .build();
    when(recipeRepository.save(any(RecipeEntity.class))).thenReturn(mockRecipe);

    // when
    ResultResponse resultResponse = recipeDivideService.processAndSaveAllData(1L, 1L);

    // then
    assertEquals("데이터 분산 저장 성공!", resultResponse.getMessage());

    // verify
    verify(recipeRepository, times(1)).save(any(RecipeEntity.class));
    verify(recipeRatingRepository, times(1)).save(any(RecipeRatingEntity.class));
    verify(manualRepository, times(1)).saveAll(anyList()); // 2개의 매뉴얼 단계가 있음
    verify(recipeIngredientRepository, times(1)).saveAll(anyList());
    verify(recipeIngredientMappingRepository, times(1)).saveAll(anyList());
  }

  @Test
  @DisplayName("데이터 없을 때 null 반환")
  void testProcessAndSaveAllDataEmptyRecipes() {
    // given
    when(tenThousandRecipeRepository.findByTrOriginIdBetween(1L, 1L)).thenReturn(Collections.emptyList());

    // when
    ResultResponse resultResponse = recipeDivideService.processAndSaveAllData(1L, 1L);

    // then
    assertEquals(null, resultResponse);
  }

  @Test
  @DisplayName("메뉴얼이 없는 경우 건너뛰고 다른 레시피는 처리")
  void testProcessAndSaveAllDataWithoutManualContents() {
    // given
    TenThousandRecipeEntity recipeWithManual = TenThousandRecipeEntity.builder()
        .trName("Recipe With Manual")
        .trLevel(LevelType.LOW)
        .trCookTime(CookingTimeType.TEN_MINUTES)
        .trThumbnail("thumbnail.jpg")
        .trMnContents("Step 1$%^Step 2")  // 메뉴얼이 있는 경우
        .trMnPictures("pic1.jpg,pic2.jpg")
        .trIngredients("Ingredient 1, Ingredient 2")
        .trRating(4.5)
        .build();
    TenThousandRecipeEntity emptyManualRecipe = TenThousandRecipeEntity.builder()
        .trName("Sample Recipe")
        .trLevel(LevelType.LOW)
        .trCookTime(CookingTimeType.TEN_MINUTES)
        .trThumbnail("thumbnail.jpg")
        .trMnContents("") // 메뉴얼이 없는 경우
        .trMnPictures("pic1.jpg,pic2.jpg")
        .trIngredients("ingredient1 100g,ingredient2 200ml")
        .trRating(4.5)
        .build();
    // 여러 레시피를 반환하도록 설정
    when(tenThousandRecipeRepository.findByTrOriginIdBetween(1L, 2L))
        .thenReturn(Arrays.asList(recipeWithManual, emptyManualRecipe));

    // when
    ResultResponse resultResponse = recipeDivideService.processAndSaveAllData(1L, 2L);

    // then
    assertNotNull(resultResponse); // 최소한 하나의 레시피가 처리되어야 하므로 null이 아니어야 함
    assertEquals("데이터 분산 저장 성공!", resultResponse.getMessage()); // 성공 코드 확인
  }

  @Test
  @DisplayName("재료가 없는 경우 건너뛰고 다른 레시피는 처리")
  void testProcessAndSaveAllDataWithSkippingWithoutIngredients() {
    // given
    TenThousandRecipeEntity recipeWithIngredients = TenThousandRecipeEntity.builder()
        .trName("Recipe With Ingredients")
        .trLevel(LevelType.LOW)
        .trCookTime(CookingTimeType.TEN_MINUTES)
        .trThumbnail("thumbnail.jpg")
        .trMnContents("Step 1$%^Step 2")
        .trMnPictures("pic1.jpg,pic2.jpg")
        .trIngredients("Ingredient 1, Ingredient 2") // 재료가 있는 경우
        .trRating(4.5)
        .build();

    TenThousandRecipeEntity emptyIngredientsRecipe = TenThousandRecipeEntity.builder()
        .trName("Recipe Without Ingredients")
        .trLevel(LevelType.LOW)
        .trCookTime(CookingTimeType.TEN_MINUTES)
        .trThumbnail("thumbnail.jpg")
        .trMnContents("Step 1$%^Step 2")
        .trMnPictures("pic1.jpg,pic2.jpg")
        .trIngredients("") // 재료가 없는 경우
        .trRating(4.5)
        .build();

    // 여러 레시피를 반환하도록 설정
    when(tenThousandRecipeRepository.findByTrOriginIdBetween(1L, 2L))
        .thenReturn(Arrays.asList(recipeWithIngredients, emptyIngredientsRecipe));

    // when
    ResultResponse resultResponse = recipeDivideService.processAndSaveAllData(1L, 2L);

    // then
    assertNotNull(resultResponse); // 최소한 하나의 레시피가 처리되어야 하므로 null이 아니어야 함
    assertEquals("데이터 분산 저장 성공!", resultResponse.getMessage()); // 성공 코드 확인
  }

  @Test
  @DisplayName("메뉴얼 길이와 사진의 길이가 같지 않을 경우 건너뛰고 다른 레시피만 저장")
  void testProcessAndSaveAllDataWithDifferentManualContentsAndPicturesLength() {
    // given
    TenThousandRecipeEntity manualWithSameLength = TenThousandRecipeEntity.builder()
        .trName("Recipe With Ingredients")
        .trLevel(LevelType.LOW)
        .trCookTime(CookingTimeType.TEN_MINUTES)
        .trThumbnail("thumbnail.jpg")
        .trMnContents("Step 1$%^Step 2")
        .trMnPictures("pic1.jpg,pic2.jpg")
        .trIngredients("Ingredient 1, Ingredient 2")
        .trRating(4.5)
        .build();
    TenThousandRecipeEntity differentLengthRecipe = TenThousandRecipeEntity.builder()
        .trName("Sample Recipe")
        .trLevel(LevelType.LOW)
        .trCookTime(CookingTimeType.TEN_MINUTES)
        .trThumbnail("thumbnail.jpg")
        .trMnContents("Step 1$%^Step 2") // 메뉴얼 내용
        .trMnPictures("pic1.jpg") // 메뉴얼 사진이 하나만 있음
        .trIngredients("ingredient1 100g,ingredient2 200ml")
        .trRating(4.5)
        .build();

    // 여러 레시피를 반환하도록 설정
    when(tenThousandRecipeRepository.findByTrOriginIdBetween(1L, 2L))
        .thenReturn(Arrays.asList(manualWithSameLength, differentLengthRecipe));

    // when
    ResultResponse resultResponse = recipeDivideService.processAndSaveAllData(1L, 2L);

    // then
    assertNotNull(resultResponse); // 최소한 하나의 레시피가 처리되어야 하므로 null이 아니어야 함
    assertEquals("데이터 분산 저장 성공!", resultResponse.getMessage()); // 성공 코드 확인
  }

  @Test
  @DisplayName("스케쥴링 함수 정상 동작")
  void testWeeklyRecipeDivide() {
    // given
    when(recipeRepository.findMaxRcpOriginId()).thenReturn(100L);
    when(tenThousandRecipeRepository.findMaxTrOriginId()).thenReturn(300L);

    List<TenThousandRecipeEntity> mockedRecipes = new ArrayList<>();
    mockedRecipes.add(scrapRecipe);
    when(tenThousandRecipeRepository.findByTrOriginIdBetween(101L, 300L)).thenReturn(mockedRecipes);

    // when
    recipeDivideService.weeklyRecipeDivide();

    // then
    // processAndSaveAllData 메서드가 lastRecipeId + 1과 크롤링 데이터의 마지막 recipeId로 호출되는지 확인
    verify(recipeRepository).findMaxRcpOriginId();
    verify(tenThousandRecipeRepository).findMaxTrOriginId();
    verify(tenThousandRecipeRepository).findByTrOriginIdBetween(101L, 300L);
  }

  @Test
  @DisplayName("레시피 테이블 데이터 저장")
  void testSaveRecipeData() {
    // given
    when(userAccessHandler.findByEmail("user@example.com")).thenReturn(user);

    RecipeEntity recipe = RecipeEntity.builder()
        .user(user)
        .rcpName(scrapRecipe.getTrName())
        .rcpLevel(scrapRecipe.getTrLevel())
        .rcpTime(scrapRecipe.getTrCookTime())
        .rcpThumbnail(scrapRecipe.getTrThumbnail())
        .rcpProvider(RecipeProvider.SCRAP)
        .build();
    when(recipeRepository.save(any(RecipeEntity.class))).thenReturn(recipe);

    // when
    RecipeEntity savedRecipe = recipeDivideService.saveRecipeData(scrapRecipe);

    // then
    assertEquals(1L, savedRecipe.getUser().getUsrId());
    assertEquals("Sample Recipe", savedRecipe.getRcpName());
    assertEquals(LevelType.LOW, savedRecipe.getRcpLevel());
    assertEquals(CookingTimeType.TEN_MINUTES, savedRecipe.getRcpTime());
    assertEquals("thumbnail.jpg", savedRecipe.getRcpThumbnail());
    assertEquals(RecipeProvider.SCRAP, savedRecipe.getRcpProvider());

    // 결과 검증
    verify(recipeRepository, times(1)).save(any(RecipeEntity.class));
  }

  @Test
  @DisplayName("평점 테이블 데이터 저장")
  void testSaveRecipeRatingData() {
    // given
    RecipeEntity recipe = RecipeEntity.builder().build();

    // when
    recipeDivideService.saveRecipeRatingData(recipe, scrapRecipe);

    // then
    assertEquals(4.5, scrapRecipe.getTrRating());

    // verify
    verify(recipeRatingRepository, times(1)).save(any(RecipeRatingEntity.class));
  }

  @Test
  @DisplayName("메뉴얼 테이블 데이터 저장")
  void testSaveManualData() {
    // given
    RecipeEntity recipe = RecipeEntity.builder().build();

    // when
    recipeDivideService.saveManualData(recipe, scrapRecipe);

    // then
    assertEquals("Step 1$%^Step 2", scrapRecipe.getTrMnContents());
    assertEquals("pic1.jpg,pic2.jpg", scrapRecipe.getTrMnPictures());

    // verify
    ArgumentCaptor<List<ManualEntity>> manualCaptor = ArgumentCaptor.forClass(List.class);
    verify(manualRepository, times(1)).saveAll(manualCaptor.capture());

    List<ManualEntity> savedManuals = manualCaptor.getValue();
    assertEquals("Step 1", savedManuals.get(0).getMnContent());
    assertEquals("pic1.jpg", savedManuals.get(0).getMnPicture());
    assertEquals("Step 2", savedManuals.get(1).getMnContent());
    assertEquals("pic2.jpg", savedManuals.get(1).getMnPicture());
  }

  @Test
  @DisplayName("manualContents가 존재하고 manualPictures가 없는 경우")
  void testSaveManualDataWithPicturesEmpty() {
    // given
    RecipeEntity recipe = RecipeEntity.builder()
        .rcpId(1L)
        .rcpName("Sample Recipe")
        .build();
    TenThousandRecipeEntity scrapedRecipe = TenThousandRecipeEntity.builder()
        .trMnContents("Step 1$%^Step 2") // 메뉴얼 내용
        .trMnPictures("") // 메뉴얼 사진 없음
        .build();

    // when
    recipeDivideService.saveManualData(recipe, scrapedRecipe);

    // then
    ArgumentCaptor<List<ManualEntity>> manualCaptor = ArgumentCaptor.forClass(List.class);
    verify(manualRepository, times(1)).saveAll(manualCaptor.capture());

    List<ManualEntity> savedManuals = manualCaptor.getValue();
    assertEquals("Step 1", savedManuals.get(0).getMnContent());
    assertEquals("", savedManuals.get(0).getMnPicture());
    assertEquals("Step 2", savedManuals.get(1).getMnContent());
    assertEquals("", savedManuals.get(1).getMnPicture());
  }

  @Test
  @DisplayName("manualContents가 존재하고 manualPictures가 없는 경우")
  void testSaveManualDataWithPicturesNull() {
    // given
    RecipeEntity recipe = RecipeEntity.builder()
        .rcpId(1L)
        .rcpName("Sample Recipe")
        .build();
    TenThousandRecipeEntity scrapedRecipe = TenThousandRecipeEntity.builder()
        .trMnContents("Step 1$%^Step 2") // 메뉴얼 내용
        .trMnPictures(null) // 메뉴얼 사진 없음
        .build();

    // when
    recipeDivideService.saveManualData(recipe, scrapedRecipe);

    // then
    ArgumentCaptor<List<ManualEntity>> manualCaptor = ArgumentCaptor.forClass(List.class);
    verify(manualRepository, times(1)).saveAll(manualCaptor.capture());

    List<ManualEntity> savedManuals = manualCaptor.getValue();
    assertEquals("Step 1", savedManuals.get(0).getMnContent());
    assertEquals("", savedManuals.get(0).getMnPicture());
    assertEquals("Step 2", savedManuals.get(1).getMnContent());
    assertEquals("", savedManuals.get(1).getMnPicture());
  }

  @Test
  @DisplayName("재료 정보 정상적으로 저장")
  void testSaveIngredientDetails() {
    // given
    RecipeEntity recipe = RecipeEntity.builder().build();
    IngredientEntity existingIngredient = IngredientEntity.builder()
        .ingId(1L)
        .ingName("ingredient1")
        .build();
    when(ingredientRepository.findByIngName("ingredient1")).thenReturn(Optional.of(existingIngredient));

    // 새로운 재료가 추가될 경우
    when(ingredientRepository.findByIngName("ingredient2")).thenReturn(Optional.empty());

    // when
    recipeDivideService.saveIngredientDetails(recipe, scrapRecipe);

    // then
    assertEquals("ingredient1 100g,ingredient2 200ml", scrapRecipe.getTrIngredients());

    // verify
    verify(recipeIngredientRepository, times(1)).saveAll(anyList());
    verify(recipeIngredientMappingRepository, times(1)).saveAll(anyList());
    verify(ingredientRepository, times(1)).findByIngName("ingredient1"); // 기존 재료 조회 확인
    verify(ingredientRepository, times(1)).findByIngName("ingredient2"); // 새로운 재료 조회 확인
    verify(ingredientRepository, times(1)).saveAll(anyList()); // 새로운 재료 저장 확인
  }

  @Test
  @DisplayName("비어있는 재료 항목은 무시하고 다른 재료는 저장")
  void testSaveIngredientDetailsWithEmptyIngredient() {
    // given
    RecipeEntity recipe = RecipeEntity.builder()
        .rcpId(1L)
        .rcpName("Sample Recipe")
        .build();

    // 재료 목록에 빈 문자열이 포함된 경우
    TenThousandRecipeEntity scrapedRecipe = TenThousandRecipeEntity.builder()
        .trIngredients("Tomato 2, , Onion 1") // 빈 재료 항목 포함
        .build();

    // when
    recipeDivideService.saveIngredientDetails(recipe, scrapedRecipe);

    // then
    ArgumentCaptor<List<RecipeIngredientEntity>> ingredientCaptor = ArgumentCaptor.forClass((Class) List.class);
    verify(recipeIngredientRepository, times(1)).saveAll(ingredientCaptor.capture());

    List<RecipeIngredientEntity> savedIngredients = ingredientCaptor.getValue();

    // Tomato와 Onion이 올바르게 저장되었는지 확인
    assertEquals(2, savedIngredients.size());
    assertEquals("Tomato", savedIngredients.get(0).getRiName());
    assertEquals("2", savedIngredients.get(0).getRiQuantity());
    assertEquals("Onion", savedIngredients.get(1).getRiName());
    assertEquals("1", savedIngredients.get(1).getRiQuantity());
  }

  @Test
  @DisplayName("비어있는 재료의 수량은 무시하고 저장")
  void testSaveIngredientDetailsWithEmptyIngredientQuantity() {
    // given
    RecipeEntity recipe = RecipeEntity.builder()
        .rcpId(1L)
        .rcpName("Sample Recipe")
        .build();

    // 재료 목록에 빈 문자열이 포함된 경우
    TenThousandRecipeEntity scrapedRecipe = TenThousandRecipeEntity.builder()
        .trIngredients("Tomato 2, Potato, Onion 1") // 빈 재료 항목 포함
        .build();

    // when
    recipeDivideService.saveIngredientDetails(recipe, scrapedRecipe);

    // then
    ArgumentCaptor<List<RecipeIngredientEntity>> ingredientCaptor = ArgumentCaptor.forClass((Class) List.class);
    verify(recipeIngredientRepository, times(1)).saveAll(ingredientCaptor.capture());

    List<RecipeIngredientEntity> savedIngredients = ingredientCaptor.getValue();

    assertEquals(3, savedIngredients.size());
    assertEquals("Tomato", savedIngredients.get(0).getRiName());
    assertEquals("2", savedIngredients.get(0).getRiQuantity());
    assertEquals("Potato", savedIngredients.get(1).getRiName());
    assertEquals("", savedIngredients.get(1).getRiQuantity());
    assertEquals("Onion", savedIngredients.get(2).getRiName());
    assertEquals("1", savedIngredients.get(2).getRiQuantity());
  }

}