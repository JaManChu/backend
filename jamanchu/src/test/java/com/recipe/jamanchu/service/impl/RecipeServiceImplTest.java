package com.recipe.jamanchu.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.auth.jwt.JwtUtil;
import com.recipe.jamanchu.component.UserAccessHandler;
import com.recipe.jamanchu.entity.CommentEntity;
import com.recipe.jamanchu.entity.IngredientEntity;
import com.recipe.jamanchu.entity.ManualEntity;
import com.recipe.jamanchu.entity.RecipeEntity;
import com.recipe.jamanchu.entity.RecipeRatingEntity;
import com.recipe.jamanchu.entity.UserEntity;
import com.recipe.jamanchu.exceptions.exception.RecipeNotFoundException;
import com.recipe.jamanchu.exceptions.exception.UnmatchedUserException;
import com.recipe.jamanchu.model.dto.request.recipe.RecipesDTO;
import com.recipe.jamanchu.model.dto.request.recipe.RecipesDeleteDTO;
import com.recipe.jamanchu.model.dto.request.recipe.RecipesSearchDTO;
import com.recipe.jamanchu.model.dto.request.recipe.RecipesUpdateDTO;
import com.recipe.jamanchu.model.dto.response.ResultResponse;
import com.recipe.jamanchu.model.dto.response.recipes.RecipesInfo;
import com.recipe.jamanchu.model.dto.response.recipes.RecipesSummary;
import com.recipe.jamanchu.model.type.CookingTimeType;
import com.recipe.jamanchu.model.type.LevelType;
import com.recipe.jamanchu.model.type.UserRole;
import com.recipe.jamanchu.repository.IngredientRepository;
import com.recipe.jamanchu.repository.ManualRepository;
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
  private ManualRepository manualRepository;

  @Mock
  private ScrapedRecipeRepository scrapedRecipeRepository;

  @Mock
  private UserAccessHandler userAccessHandler;

  @Mock
  private JwtUtil jwtUtil;

  @Mock
  private HttpServletRequest request;

  @InjectMocks
  private RecipeServiceImpl recipeService;

  private UserEntity user;
  private List<ManualEntity> manuals;
  private List<IngredientEntity> ingredients;
  private RecipeRatingEntity rating;
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

    ingredients = new ArrayList<>();
    ingredients.add(IngredientEntity.builder()
        .name("돼지고기")
        .quantity("200g")
        .build());

    ingredients.add(IngredientEntity.builder()
        .name("달걀")
        .quantity("2개")
        .build());

    manuals = new ArrayList<>();
    manuals.add(ManualEntity.builder()
        .manualContent("고기를 구워주세요.")
        .manualPicture("manual_picture_1.jpg")
        .build());

    manuals.add(ManualEntity.builder()
        .manualContent("달걀을 삶아주세요.")
        .manualPicture("manual_picture_2.jpg")
        .build());

    recipesDTO = new RecipesDTO(
        "recipeName",
        LevelType.LOW,
        CookingTimeType.FIFTEEN_MINUTES,
        "thumbnail",
        ingredients,
        manuals
    );

    recipesUpdateDTO = new RecipesUpdateDTO(
        "recipeUpdateName",
        LevelType.LOW,
        CookingTimeType.TEN_MINUTES,
        "thumbnail.jpg",
        ingredients,
        manuals,
        1L
    );

    recipe = RecipeEntity.builder()
        .id(1L)
        .user(user)
        .name(recipesDTO.getRecipeName())
        .level(recipesDTO.getLevel())
        .time(recipesDTO.getCookingTime())
        .thumbnail(recipesDTO.getRecipeImage())
        .ingredients(recipesDTO.getIngredients())
        .manuals(recipesDTO.getManuals())
        .build();
  }

  @Test
  @DisplayName("레시피 등록 성공")
  void registerRecipe_Success() {
    // given
    when(jwtUtil.getUserId(request.getHeader("access-token"))).thenReturn(user.getUserId());
    when(userAccessHandler.findByUserId(user.getUserId())).thenReturn(user);

    // when
    ResultResponse result = recipeService.registerRecipe(request, recipesDTO);

    // then
    assertEquals("레시피 등록 성공!", result.getMessage());

    // verify
    verify(recipeRepository, times(1)).save(any(RecipeEntity.class));
    verify(ingredientRepository, times(1)).saveAll(anyList());
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
        .level(recipesDTO.getLevel())
        .time(recipesDTO.getCookingTime())
        .thumbnail(recipesDTO.getRecipeImage())
        .ingredients(recipesDTO.getIngredients())
        .manuals(recipesDTO.getManuals())
        .build();

    when(jwtUtil.getUserId(request.getHeader("access-token"))).thenReturn(user.getUserId());
    when(userAccessHandler.findByUserId(1L)).thenReturn(user);
    when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
    when(ingredientRepository.findAllByRecipeId(1L)).thenReturn(Optional.of(ingredients));
    when(manualRepository.findAllByRecipeId(1L)).thenReturn(Optional.of(manuals));

    // When
    ResultResponse result = recipeService.updateRecipe(request, recipesUpdateDTO);

    // Then
    assertEquals("레시피 수정 성공!", result.getMessage());

    // verify
    verify(recipeRepository, times(1)).findById(1L);
    verify(ingredientRepository, times(1)).deleteAllByRecipeId(1L);
    verify(ingredientRepository, times(1)).saveAll(anyList());
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

    when(jwtUtil.getUserId(request.getHeader("access-token"))).thenReturn(2L);
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

    when(jwtUtil.getUserId(request.getHeader("access-token"))).thenReturn(user.getUserId());
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

    when(jwtUtil.getUserId(request.getHeader("access-token"))).thenReturn(2L);
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
  @DisplayName("전체 레시피 목록 조회 성공")
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

    when(recipeRepository.findAll(any(Pageable.class))).thenReturn(recipePage);

    // when
    ResultResponse result = recipeService.getRecipes(0, 10);

    // then
    assertEquals("전체 레시피 조회 성공!", result.getMessage());
    List<RecipesSummary> summaries = (List<RecipesSummary>) result.getData();
    assertEquals(2, summaries.size());
    assertEquals("Recipe1", summaries.get(0).getRecipeName());
    assertEquals("Recipe2", summaries.get(1).getRecipeName());
  }

  @Test
  @DisplayName("전체 레시피 목록 조회 실패 - 레시피 없음")
  void getRecipes_Fail_NoRecipes() {
    // given
    Page<RecipeEntity> emptyPage = Page.empty();

    when(recipeRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

    // when & then
    assertThrows(RecipeNotFoundException.class,
        () -> recipeService.getRecipes(0, 10));
  }

  @Test
  @DisplayName("레시피 조건 검색 성공")
  void searchRecipes_Success() {
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

    when(recipeRepository.searchAndRecipes(any(), any(), any(), anyLong(), any(Pageable.class))).thenReturn(recipePage);

    // when
    ResultResponse result = recipeService.searchRecipes(searchDTO, 0, 10);

    // then
    assertEquals("레시피 조회 성공!", result.getMessage());
    List<RecipesSummary> summaries = (List<RecipesSummary>) result.getData();
    assertEquals(1, summaries.size());
    assertEquals("Recipe1", summaries.get(0).getRecipeName());
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

    when(recipeRepository.searchAndRecipes(any(), any(), any(), anyLong(), any(Pageable.class))).thenReturn(emptyPage);
    when(recipeRepository.searchOrRecipes(any(), any(), any(), any(Pageable.class))).thenReturn(emptyPage);

    // when & then
    assertThrows(RecipeNotFoundException.class,
        () -> recipeService.searchRecipes(searchDTO, 0, 10));
  }

  @Test
  @DisplayName("레시피 상세 조회 성공")
  void getRecipeDetail_Success() {
    // given
    List<CommentEntity> commentEntities = new ArrayList<>();
    commentEntities.add(CommentEntity.builder()
        .commentId(1L)
        .user(user)
        .recipe(recipe)
        .commentContent("Great recipe!")
        .commentLike(5.0)
        .build());

    recipe = RecipeEntity.builder()
        .id(1L)
        .user(user)
        .name("recipeName")
        .level(LevelType.LOW)
        .time(CookingTimeType.FIFTEEN_MINUTES)
        .thumbnail("thumbnail")
        .ingredients(ingredients)
        .manuals(manuals)
        .comments(commentEntities)
        .build();

    when(recipeRepository.findById(recipe.getId())).thenReturn(Optional.of(recipe));

    // when
    ResultResponse result = recipeService.getRecipeDetail(recipe.getId());

    // then
    assertEquals("레시피 상세 조회 성공", result.getMessage());
    RecipesInfo recipesInfo = (RecipesInfo) result.getData();
    assertEquals(recipe.getName(), recipesInfo.getRecipeName());
    assertEquals(recipe.getUser().getNickname(), recipesInfo.getRecipeAuthor());
    assertFalse(recipesInfo.getIngredients().isEmpty());
    assertFalse(recipesInfo.getRecipesManuals().getRecipesManuals().isEmpty());
    assertFalse(recipesInfo.getComments().getComments().isEmpty());
  }

  @Test
  @DisplayName("레시피 평점으로 조회 성공")
  void getRecipesByRating_Success() {
    // given
    int page = 0;
    int size = 10;

    List<RecipeEntity> recipeList = new ArrayList<>();
    recipeList.add(recipe);

    Pageable pageable = PageRequest.of(page, size);
    Page<RecipeEntity> recipePage = new PageImpl<>(recipeList);

    when(recipeRepository.findAllOrderByRating(pageable)).thenReturn(recipePage);

    // when
    ResultResponse result = recipeService.getRecipesByRating(0, 10);

    // then
    assertEquals("인기 레시피 조회 성공", result.getMessage());
    assertNotNull(result.getData());
    List<RecipesSummary> recipesSummaries = (List<RecipesSummary>) result.getData();
    assertEquals(1, recipesSummaries.size());
    assertEquals(recipe.getId(), recipesSummaries.get(0).getRecipeId());
    assertEquals(recipe.getName(), recipesSummaries.get(0).getRecipeName());
    assertEquals(recipe.getUser().getNickname(), recipesSummaries.get(0).getRecipeAuthor());
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
        () -> recipeService.getRecipesByRating(page, size));
  }

  @Test
  @DisplayName("레시피 스크랩 성공")
  void scrapedRecipe_Success() {
    // given
    when(jwtUtil.getUserId(request.getHeader("access-token"))).thenReturn(user.getUserId());
    when(userAccessHandler.findByUserId(user.getUserId())).thenReturn(user);
    when(recipeRepository.findById(recipe.getId())).thenReturn(Optional.of(recipe));

    // when
    ResultResponse result = recipeService.scrapedRecipe(request, recipe.getId());

    // then
    assertEquals("레시피 찜하기 성공", result.getMessage());

    // verify
    verify(scrapedRecipeRepository, times(1)).save(any());
  }
}