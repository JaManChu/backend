package com.recipe.jamanchu.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.auth.jwt.JwtUtil;
import com.recipe.jamanchu.component.UserAccessHandler;
import com.recipe.jamanchu.entity.IngredientEntity;
import com.recipe.jamanchu.entity.ManualEntity;
import com.recipe.jamanchu.entity.RecipeEntity;
import com.recipe.jamanchu.entity.RecipeIngredientMappingEntity;
import com.recipe.jamanchu.entity.RecipeRatingEntity;
import com.recipe.jamanchu.entity.ScrapedRecipeEntity;
import com.recipe.jamanchu.entity.UserEntity;
import com.recipe.jamanchu.exceptions.exception.RecipeNotFoundException;
import com.recipe.jamanchu.exceptions.exception.UnmatchedUserException;
import com.recipe.jamanchu.model.dto.request.recipe.RecipesDTO;
import com.recipe.jamanchu.model.dto.request.recipe.RecipesDeleteDTO;
import com.recipe.jamanchu.model.dto.request.recipe.RecipesSearchDTO;
import com.recipe.jamanchu.model.dto.request.recipe.RecipesUpdateDTO;
import com.recipe.jamanchu.model.dto.response.ResultResponse;
import com.recipe.jamanchu.model.dto.response.ingredients.Ingredient;
import com.recipe.jamanchu.model.dto.response.recipes.RecipesInfo;
import com.recipe.jamanchu.model.dto.response.recipes.RecipesManual;
import com.recipe.jamanchu.model.dto.response.recipes.RecipesSummary;
import com.recipe.jamanchu.model.type.CookingTimeType;
import com.recipe.jamanchu.model.type.LevelType;
import com.recipe.jamanchu.model.type.ScrapedType;
import com.recipe.jamanchu.model.type.TokenType;
import com.recipe.jamanchu.model.type.UserRole;
import com.recipe.jamanchu.repository.IngredientRepository;
import com.recipe.jamanchu.repository.ManualRepository;
import com.recipe.jamanchu.repository.RecipeIngredientMappingRepository;
import com.recipe.jamanchu.repository.RecipeRatingRepository;
import com.recipe.jamanchu.repository.RecipeRepository;
import com.recipe.jamanchu.repository.ScrapedRecipeRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class RecipeServiceImplTest {

  @Mock
  private RecipeRepository recipeRepository;

  @Mock
  private IngredientRepository ingredientRepository;

  @Mock
  private RecipeIngredientMappingRepository recipeIngredientMappingRepository;

  @Mock
  private ManualRepository manualRepository;

  @Mock
  private ScrapedRecipeRepository scrapedRecipeRepository;

  @Mock
  private RecipeRatingRepository ratingRepository;

  @Mock
  private UserAccessHandler userAccessHandler;

  @Mock
  private JwtUtil jwtUtil;

  @Mock
  private HttpServletRequest request;

  @InjectMocks
  private RecipeServiceImpl recipeService;

  private UserEntity user;
  private List<IngredientEntity> ingredientEntities;
  private List<ManualEntity> manualEntities;
  private RecipeEntity recipe;
  private RecipesDTO recipesDTO;
  private RecipesUpdateDTO recipesUpdateDTO;

  @BeforeEach
  void setUp() {
    user = UserEntity.builder()
        .userId(1L)
        .email("test@example.com")
        .nickname("nickname")
        .password("password")
        .role(UserRole.USER)
        .provider(null)
        .providerId(null)
        .build();

    List<Ingredient> ingredients =
        List.of(
            new Ingredient("재료", "재료 양"),
            new Ingredient("재료1", "재료 양1")
        );
    List<RecipesManual> manuals =
        List.of(
            new RecipesManual("레시피 순서", "레시피 이미지"),
            new RecipesManual("레시피 순서1", "레시피 이미지1")
        );

    recipesDTO = new RecipesDTO(
        "recipeName",
        LevelType.LOW,
        CookingTimeType.FIFTEEN_MINUTES,
        null,
        ingredients,
        manuals
    );

    recipesUpdateDTO = new RecipesUpdateDTO(
        "recipeUpdateName",
        LevelType.LOW,
        CookingTimeType.TEN_MINUTES,
        null,
        ingredients,
        manuals,
        1L
    );

    ingredientEntities = new ArrayList<>();
    for (int i = 0; i < recipesDTO.getRecipeIngredients().size(); i++) {
      IngredientEntity ingredient = IngredientEntity.builder()
          .recipe(recipe)
          .name(recipesDTO.getRecipeIngredients().get(i).getIngredientName())
          .quantity(recipesDTO.getRecipeIngredients().get(i).getIngredientQuantity())
          .build();

      ingredientEntities.add(ingredient);
    }

    manualEntities = new ArrayList<>();
    for (int i = 0; i < recipesDTO.getRecipeOrderContents().size(); i++) {
      ManualEntity manual = ManualEntity.builder()
          .recipe(recipe)
          .manualContent(recipesDTO.getRecipeOrderContents().get(i).getRecipeOrderContent())
          .manualPicture(recipesDTO.getRecipeOrderContents().get(i).getRecipeOrderImage())
          .build();

      manualEntities.add(manual);
    }

    recipe = RecipeEntity.builder()
        .id(1L)
        .user(user)
        .name(recipesDTO.getRecipeName())
        .level(recipesDTO.getRecipeLevel())
        .time(recipesDTO.getRecipeCookingTime())
        .thumbnail(String.valueOf(recipesDTO.getRecipeThumbnail()))
        .ingredients(ingredientEntities)
        .manuals(manualEntities)
        .build();
  }

  @Test
  @DisplayName("레시피 등록 성공")
  void registerRecipe_Success() {
    // given
    when(jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue()))).thenReturn(user.getUserId());
    when(userAccessHandler.findByUserId(user.getUserId())).thenReturn(user);
    when(ingredientRepository.saveAll(anyList())).thenReturn(new ArrayList<>());
    when(recipeIngredientMappingRepository.saveAll(anyList())).thenReturn(new ArrayList<>());

    // when
    ResultResponse result = recipeService.registerRecipe(request, recipesDTO);

    // then
    assertEquals("레시피 등록 성공!", result.getMessage());

    // verify
    verify(recipeRepository, times(1)).save(any(RecipeEntity.class));
    verify(ingredientRepository, times(1)).saveAll(anyList());
    verify(recipeIngredientMappingRepository, times(1)).saveAll(anyList());
    verify(manualRepository, times(1)).saveAll(anyList());
  }

  @Test
  @DisplayName("레시피 수정 성공")
  void updateRecipe_Success() {
    // Given
    recipe = RecipeEntity.builder()
        .id(1L)
        .user(user)
        .name(recipesDTO.getRecipeName())
        .level(recipesDTO.getRecipeLevel())
        .time(recipesDTO.getRecipeCookingTime())
        .thumbnail(String.valueOf(recipesDTO.getRecipeThumbnail()))
        .ingredients(ingredientEntities)
        .manuals(manualEntities)
        .build();

    List<RecipeIngredientMappingEntity> recipeIngredientMappings = new ArrayList<>();
    for (IngredientEntity ingredient : ingredientEntities) {
      RecipeIngredientMappingEntity mapping = RecipeIngredientMappingEntity.builder()
          .recipe(recipe)
          .ingredient(ingredient)
          .build();

      recipeIngredientMappings.add(mapping);
    }

    when(jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue()))).thenReturn(user.getUserId());
    when(userAccessHandler.findByUserId(1L)).thenReturn(user);
    when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));

    // When
    ResultResponse result = recipeService.updateRecipe(request, recipesUpdateDTO);

    // Then
    assertEquals("레시피 수정 성공!", result.getMessage());

    // verify
    verify(recipeRepository, times(1)).findById(1L);
    verify(ingredientRepository, times(1)).deleteAllByRecipeId(1L);
    verify(ingredientRepository, times(1)).saveAll(anyList());
    verify(recipeIngredientMappingRepository, times(1)).deleteAllByRecipeId(1L);
    verify(recipeIngredientMappingRepository, times(1)).saveAll(anyList());
    verify(manualRepository, times(1)).deleteAllByRecipeId(1L);
    verify(manualRepository, times(1)).saveAll(anyList());
  }

  @Test
  @DisplayName("레시피 수정 실패 - 등록한 유저와 수정하는 유저가 다를 때")
  void updateRecipe_FailUnMatchedUser() {
    // given
    UserEntity requestUser = UserEntity.builder()
        .userId(2L)
        .build();

    when(jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue()))).thenReturn(2L);
    when(userAccessHandler.findByUserId(requestUser.getUserId())).thenReturn(requestUser);
    when(recipeRepository.findById(recipe.getId())).thenReturn(Optional.ofNullable(recipe));

    // when & then
    assertThrows(UnmatchedUserException.class,
        () -> recipeService.updateRecipe(request, recipesUpdateDTO));

    // verify
    verify(recipeRepository, times(1)).findById(1L);
    verify(ingredientRepository, times(0)).deleteAllByRecipeId(1L);
    verify(ingredientRepository, times(0)).saveAll(anyList());
    verify(manualRepository, times(0)).deleteAllByRecipeId(1L);
    verify(manualRepository, times(0)).saveAll(anyList());
  }

  @Test
  @DisplayName("레시피 삭제 성공")
  void deleteRecipe_Success() {
    // Given
    RecipesDeleteDTO deleteDTO = new RecipesDeleteDTO(
        1L
    );

    when(jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue()))).thenReturn(user.getUserId());
    when(userAccessHandler.findByUserId(1L)).thenReturn(user);
    when(recipeRepository.findById(recipe.getId())).thenReturn(Optional.of(recipe));

    // When
    ResultResponse result = recipeService.deleteRecipe(request, deleteDTO);

    // Then
    assertEquals("레시피를 정상적으로 삭제하였습니다.", result.getMessage());

    // verify
    verify(recipeRepository, times(1)).deleteById(recipe.getId());
  }

  @Test
  @DisplayName("레시피 삭제 실패 - 등록한 유저와 삭제하는 유저가 다를 때")
  void deleteRecipe_FailUnMatchedUser() {
    // given
    UserEntity requestUser = UserEntity.builder()
        .userId(2L)
        .build();
    RecipesDeleteDTO deleteDTO = new RecipesDeleteDTO(
        1L
    );

    when(jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue()))).thenReturn(2L);
    when(userAccessHandler.findByUserId(requestUser.getUserId())).thenReturn(requestUser);
    when(recipeRepository.findById(recipe.getId())).thenReturn(Optional.ofNullable(recipe));

    // when & then
    assertThrows(UnmatchedUserException.class,
        () -> recipeService.deleteRecipe(request, deleteDTO));

    // verify
    verify(recipeRepository, times(1)).findById(1L);
    verify(recipeRepository, times(0)).deleteById(recipe.getId());
  }

  @Test
  @DisplayName("전체 레시피 목록 조회 성공 - token이 없을 때")
  void getRecipes_Success() {
    // given
    List<RecipeEntity> recipeEntities = List.of(
        RecipeEntity.builder()
            .id(1L)
            .name("Recipe1")
            .user(user)
            .level(LevelType.LOW)
            .time(CookingTimeType.TEN_MINUTES)
            .thumbnail("thumbnail1")
            .build(),
        RecipeEntity.builder()
            .id(2L)
            .name("Recipe2")
            .user(user)
            .level(LevelType.MEDIUM)
            .time(CookingTimeType.TWENTY_MINUTES)
            .thumbnail("thumbnail2")
            .build()
    );

    Page<RecipeEntity> recipePage = new PageImpl<>(recipeEntities);

    when(request.getHeader(TokenType.ACCESS.getValue())).thenReturn(null);
    when(recipeRepository.findAll(any(Pageable.class))).thenReturn(recipePage);

    // when
    ResultResponse result = recipeService.getRecipes(request, 0, 10);

    // then
    assertEquals("전체 레시피 조회 성공!", result.getMessage());
    List<RecipesSummary> summaries = (List<RecipesSummary>) result.getData();
    assertEquals(2, summaries.size());
    assertEquals("Recipe1", summaries.get(0).getRecipeName());
    assertEquals("Recipe2", summaries.get(1).getRecipeName());

    // verify
    verify(recipeRepository, times(1)).findAll(any(Pageable.class));
    verify(scrapedRecipeRepository, never()).findRecipeIdsByUserIdAndScrapedType(anyLong(), any());
  }

  @Test
  @DisplayName("SCRAPED한 레시피 제외하고 목록 조회 성공")
  void getRecipes_WithScrapedExclusion_Success() {
    // given
    List<RecipeEntity> recipeEntities = List.of(
        RecipeEntity.builder()
            .id(2L)
            .name("Recipe2")
            .user(user)
            .level(LevelType.MEDIUM)
            .time(CookingTimeType.TWENTY_MINUTES)
            .thumbnail("thumbnail2")
            .build()
    );
    Page<RecipeEntity> recipePage = new PageImpl<>(recipeEntities);

    String token = "access-token";
    Long userId = 1L;
    List<Long> scrapedRecipeIds = List.of(1L);

    when(request.getHeader(TokenType.ACCESS.getValue())).thenReturn(token);
    when(jwtUtil.getUserId(token)).thenReturn(userId);
    when(scrapedRecipeRepository.findRecipeIdsByUserIdAndScrapedType(userId, ScrapedType.SCRAPED)).thenReturn(scrapedRecipeIds);
    when(recipeRepository.findByIdNotIn(eq(scrapedRecipeIds), any(Pageable.class))).thenReturn(recipePage);

    // when
    ResultResponse result = recipeService.getRecipes(request, 0, 10);

    // then
    assertEquals("전체 레시피 조회 성공!", result.getMessage());
    List<RecipesSummary> summaries = (List<RecipesSummary>) result.getData();
    assertEquals(1, summaries.size());
    assertEquals("Recipe2", summaries.get(0).getRecipeName());

    // verify
    verify(scrapedRecipeRepository, times(1)).findRecipeIdsByUserIdAndScrapedType(user.getUserId(), ScrapedType.SCRAPED);
    verify(recipeRepository, times(1)).findByIdNotIn(eq(scrapedRecipeIds), any(Pageable.class));
  }

  @Test
  @DisplayName("전체 레시피 목록 조회 실패 - 레시피 없음")
  void getRecipes_Fail_NoRecipes() {
    // given
    Page<RecipeEntity> emptyPage = Page.empty();

    when(recipeRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

    // when & then
    assertThrows(RecipeNotFoundException.class,
        () -> recipeService.getRecipes(request, 0, 10));
  }

  @Test
  @DisplayName("레시피 조건 검색 성공 - 스크랩한 레시피가 없는 경우")
  void searchRecipes_Success_NoScrapedRecipes() {
    // given
    RecipesSearchDTO searchDTO = new RecipesSearchDTO(
        List.of("돼지고기"),
        LevelType.LOW,
        CookingTimeType.TEN_MINUTES
    );

    List<RecipeEntity> recipeEntities = List.of(
        RecipeEntity.builder()
            .id(1L)
            .name("Recipe1")
            .user(user)
            .level(LevelType.LOW)
            .time(CookingTimeType.TEN_MINUTES)
            .thumbnail("thumbnail1")
            .build()
    );

    Page<RecipeEntity> recipePage = new PageImpl<>(recipeEntities);

    when(recipeRepository.searchAndRecipes(eq(searchDTO.getRecipeLevel()),
        eq(searchDTO.getRecipeCookingTime()),
        anyList(),
        eq((long) searchDTO.getIngredients().size()),
        any(Pageable.class)))
        .thenReturn(recipePage);

    // when
    ResultResponse result = recipeService.searchRecipes(request, searchDTO, 0, 10);

    // then
    assertEquals("레시피 조회 성공!", result.getMessage());
    List<RecipesSummary> summaries = (List<RecipesSummary>) result.getData();
    assertEquals(1, summaries.size());
    assertEquals("Recipe1", summaries.get(0).getRecipeName());

    // verify
    verify(recipeRepository, times(1)).searchAndRecipes(any(), any(), any(), any(), any(Pageable.class));
    verify(recipeRepository, never()).searchOrRecipes(any(), any(), any(), any(Pageable.class));
  }

  @Test
  @DisplayName("레시피 조건 검색 성공 - 스크랩한 레시피가 있는 경우")
  void searchRecipes_Success_WithScrapedRecipes() {
    // given
    RecipesSearchDTO searchDTO = new RecipesSearchDTO(
        List.of("돼지고기"),
        LevelType.LOW,
        CookingTimeType.TEN_MINUTES
    );

    List<RecipeEntity> recipeEntities = List.of(
        RecipeEntity.builder()
            .id(2L)
            .name("Recipe2")
            .user(user)
            .level(LevelType.LOW)
            .time(CookingTimeType.TEN_MINUTES)
            .thumbnail("thumbnail2")
            .build()
    );

    Page<RecipeEntity> recipePage = new PageImpl<>(recipeEntities);

    String token = "access-token";
    Long userId = 1L;
    List<Long> scrapedRecipeIds = List.of(1L);

    when(request.getHeader(TokenType.ACCESS.getValue())).thenReturn(token);
    when(jwtUtil.getUserId(token)).thenReturn(userId);
    when(scrapedRecipeRepository.findRecipeIdsByUserIdAndScrapedType(userId, ScrapedType.SCRAPED)).thenReturn(scrapedRecipeIds);
    when(recipeRepository.searchAndRecipesIdNotIn(eq(searchDTO.getRecipeLevel()),
        eq(searchDTO.getRecipeCookingTime()),
        anyList(),
        eq((long) searchDTO.getIngredients().size()),
        eq(scrapedRecipeIds),
        any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of())); // 먼저 AND 쿼리가 비어있도록 설정

    when(recipeRepository.searchOrRecipesIdNotIn(eq(searchDTO.getRecipeLevel()),
        eq(searchDTO.getRecipeCookingTime()),
        anyList(),
        eq(scrapedRecipeIds),
        any(Pageable.class)))
        .thenReturn(recipePage); // OR 쿼리에서 결과를 반환하도록 설정

    // when
    ResultResponse result = recipeService.searchRecipes(request, searchDTO, 0, 10);

    // then
    assertEquals("레시피 조회 성공!", result.getMessage());
    List<RecipesSummary> summaries = (List<RecipesSummary>) result.getData();
    assertEquals(1, summaries.size());
    assertEquals("Recipe2", summaries.get(0).getRecipeName());

    // verify
    verify(recipeRepository, times(1)).searchAndRecipesIdNotIn(any(), any(), any(), any(), any(), any(Pageable.class));
    verify(recipeRepository, times(1)).searchOrRecipesIdNotIn(any(), any(), any(), any(), any(Pageable.class));
  }

  @Test
  @DisplayName("레시피 조건 검색 실패 - 조건에 맞는 레시피 없음")
  void searchRecipes_Fail_NoRecipes() {
    // given
    RecipesSearchDTO searchDTO = new RecipesSearchDTO(
        List.of("돼지고기"),
        LevelType.LOW,
        CookingTimeType.TEN_MINUTES
    );
    Page<RecipeEntity> emptyPage = Page.empty();

    when(recipeRepository.searchAndRecipes(any(), any(), any(), anyLong(),
        any(Pageable.class))).thenReturn(emptyPage);
    when(recipeRepository.searchOrRecipes(any(), any(), any(), any(Pageable.class))).thenReturn(
        emptyPage);

    // when & then
    assertThrows(RecipeNotFoundException.class,
        () -> recipeService.searchRecipes(request, searchDTO, 0, 10));
  }

  @Test
  @DisplayName("레시피 상세 조회 성공")
  void getRecipeDetail_Success() {
    // given
    List<RecipeRatingEntity> ratingEntities = new ArrayList<>();
    ratingEntities.add(RecipeRatingEntity.builder()
        .recipeRatingId(1L)
        .user(user)
        .recipe(recipe)
        .rating(4.5)
        .build());

    recipe = RecipeEntity.builder()
        .id(1L)
        .user(user)
        .name("recipeName")
        .level(LevelType.LOW)
        .time(CookingTimeType.FIFTEEN_MINUTES)
        .thumbnail("thumbnail")
        .ingredients(ingredientEntities)
        .manuals(manualEntities)
        .rating(ratingEntities)
        .build();

    when(recipeRepository.findById(recipe.getId())).thenReturn(Optional.of(recipe));
    when(ratingRepository.findAverageRatingByRecipeId(recipe.getId())).thenReturn(4.5);

    // when
    ResultResponse result = recipeService.getRecipeDetail(recipe.getId());

    // then
    assertEquals("레시피 상세 조회 성공", result.getMessage());
    RecipesInfo recipesInfo = (RecipesInfo) result.getData();
    assertEquals(recipe.getName(), recipesInfo.getRecipeName());
    assertEquals(recipe.getUser().getNickname(), recipesInfo.getRecipeAuthor());
    assertEquals(4.5, recipesInfo.getRecipeRating());
    assertFalse(recipesInfo.getRecipeIngredients().isEmpty());
    assertFalse(recipesInfo.getRecipesManuals().isEmpty());
  }

  @Test
  @DisplayName("레시피 평점으로 조회 성공 - SCRAPED 레시피 제외")
  void getRecipesByRating_Success_WithScrapedRecipeIds() {
    // given
    List<RecipeEntity> recipeEntities = List.of(
        RecipeEntity.builder()
            .id(2L)
            .name("Recipe2")
            .user(user)
            .level(LevelType.MEDIUM)
            .time(CookingTimeType.TWENTY_MINUTES)
            .thumbnail("thumbnail2")
            .build()
    );
    Page<RecipeEntity> recipePage = new PageImpl<>(recipeEntities);

    String token = "access-token";
    Long userId = 1L;
    List<Long> scrapedRecipeIds = List.of(2L);

    when(request.getHeader(TokenType.ACCESS.getValue())).thenReturn(token);
    when(jwtUtil.getUserId(token)).thenReturn(userId);

    // Mocking repository methods
    when(scrapedRecipeRepository.findRecipeIdsByUserIdAndScrapedType(userId, ScrapedType.SCRAPED)).thenReturn(scrapedRecipeIds);
    when(recipeRepository.findByIdNotInOrderByRating(eq(scrapedRecipeIds), any(Pageable.class))).thenReturn(recipePage); // Empty for no scraped case

    // when
    ResultResponse result = recipeService.getRecipesByRating(request, 0, 10);

    // then
    assertEquals("인기 레시피 조회 성공", result.getMessage());
    assertNotNull(result.getData());
    List<RecipesSummary> recipesSummaries = (List<RecipesSummary>) result.getData();
    assertEquals(1, recipesSummaries.size());
    assertEquals(2, recipesSummaries.get(0).getRecipeId());
    assertEquals("Recipe2", recipesSummaries.get(0).getRecipeName());
    assertEquals(recipe.getUser().getNickname(), recipesSummaries.get(0).getRecipeAuthor());
    assertEquals(0.0, recipesSummaries.get(0).getRecipeRating());

    // verify
    verify(scrapedRecipeRepository, times(1)).findRecipeIdsByUserIdAndScrapedType(user.getUserId(), ScrapedType.SCRAPED);
    verify(recipeRepository,times(1)).findByIdNotInOrderByRating(eq(scrapedRecipeIds), any(Pageable.class));
  }

  @Test
  @DisplayName("레시피 평점으로 조회 성공 - SCRAPED 레시피 없음")
  void getRecipesByRating_Success_WithoutScrapedRecipeIds() {
    // given
    List<RecipeRatingEntity> ratingEntities = new ArrayList<>();
    ratingEntities.add(RecipeRatingEntity.builder()
        .recipeRatingId(1L)
        .user(user)
        .recipe(recipe)
        .rating(4.5)
        .build());
    List<RecipeEntity> recipeList = new ArrayList<>();
    RecipeEntity recipe1 = RecipeEntity.builder()
        .id(2L)
        .name("recipe2")
        .user(user)
        .rating(ratingEntities)
        .build();
    recipeList.add(recipe);
    recipeList.add(recipe1);

    Page<RecipeEntity> recipePage = new PageImpl<>(recipeList);

    // Mocking repository methods
    when(recipeRepository.findAllOrderByRating(any(Pageable.class))).thenReturn(recipePage);

    // when
    ResultResponse result = recipeService.getRecipesByRating(request, 0, 10);

    // then
    assertEquals("인기 레시피 조회 성공", result.getMessage());
    assertNotNull(result.getData());
    List<RecipesSummary> recipesSummaries = (List<RecipesSummary>) result.getData();
    assertEquals(2, recipesSummaries.size());
    assertEquals(recipe.getId(), recipesSummaries.get(0).getRecipeId());
    assertEquals(recipe.getName(), recipesSummaries.get(0).getRecipeName());
    assertEquals(recipe.getUser().getNickname(), recipesSummaries.get(0).getRecipeAuthor());
    assertEquals(0.0, recipesSummaries.get(0).getRecipeRating());
  }

  @Test
  @DisplayName("레시피 평점으로 조회 실패 (레시피 없음)")
  void getRecipesByRating_FailNotFoundRecipe() {
    // given
    int page = 0;
    int size = 10;

    Pageable pageable = PageRequest.of(page, size);
    Page<RecipeEntity> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

    when(recipeRepository.findAllOrderByRating(pageable)).thenReturn(emptyPage);

    // when & Then
    assertThrows(RecipeNotFoundException.class,
        () -> recipeService.getRecipesByRating(request, page, size));
  }

  @Test
  @DisplayName("레시피 스크랩 취소 - SCRAPED -> CANCELED")
  void scrapedRecipe_Success_ScrapedToCanceled() {
    // given
    ScrapedRecipeEntity scrapedRecipe = ScrapedRecipeEntity.builder()
        .user(user)
        .recipe(recipe)
        .scrapedType(ScrapedType.SCRAPED)
        .build();

    when(jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue()))).thenReturn(user.getUserId());
    when(userAccessHandler.findByUserId(user.getUserId())).thenReturn(user);
    when(recipeRepository.findById(recipe.getId())).thenReturn(Optional.of(recipe));
    when(scrapedRecipeRepository.findByUserAndRecipe(user, recipe)).thenReturn(scrapedRecipe);

    // when
    ResultResponse result = recipeService.scrapedRecipe(request, recipe.getId());

    // then
    assertEquals("레시피 찜하기 취소 성공", result.getMessage());
    assertEquals(ScrapedType.CANCELED, result.getData()); // SCRAPED -> CANCELED

    // verify
    verify(scrapedRecipeRepository, times(1)).findByUserAndRecipe(any(), any());
    verify(scrapedRecipeRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("레시피 스크랩 성공 - CANCELED -> SCRAPED")
  void scrapedRecipe_Success_CanceledToScraped() {
    // given
    ScrapedRecipeEntity scrapedRecipe = ScrapedRecipeEntity.builder()
        .user(user)
        .recipe(recipe)
        .scrapedType(ScrapedType.CANCELED)
        .build();

    when(jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue()))).thenReturn(user.getUserId());
    when(userAccessHandler.findByUserId(user.getUserId())).thenReturn(user);
    when(recipeRepository.findById(recipe.getId())).thenReturn(Optional.of(recipe));
    when(scrapedRecipeRepository.findByUserAndRecipe(user, recipe)).thenReturn(scrapedRecipe);

    // when
    ResultResponse result = recipeService.scrapedRecipe(request, recipe.getId());

    // then
    assertEquals("레시피 찜하기 성공", result.getMessage());
    assertEquals(ScrapedType.SCRAPED, result.getData());

    // verify
    verify(scrapedRecipeRepository, times(1)).findByUserAndRecipe(any(), any());
    verify(scrapedRecipeRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("레시피 스크랩 성공 - 기존 스크랩 없음")
  void scrapedRecipe_Success_NewScraped() {
    // given
    when(jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue()))).thenReturn(user.getUserId());
    when(userAccessHandler.findByUserId(user.getUserId())).thenReturn(user);
    when(recipeRepository.findById(recipe.getId())).thenReturn(Optional.of(recipe));
    when(scrapedRecipeRepository.findByUserAndRecipe(user, recipe)).thenReturn(null); // 기존 스크랩 없음

    // when
    ResultResponse result = recipeService.scrapedRecipe(request, recipe.getId());

    // then
    assertEquals("레시피 찜하기 성공", result.getMessage());
    assertEquals(ScrapedType.SCRAPED, result.getData());

    // verify
    verify(scrapedRecipeRepository, times(1)).findByUserAndRecipe(any(), any());
    verify(scrapedRecipeRepository, times(1)).save(any());
  }
}