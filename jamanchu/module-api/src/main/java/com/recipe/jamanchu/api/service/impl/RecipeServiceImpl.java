package com.recipe.jamanchu.api.service.impl;

import static com.recipe.jamanchu.domain.model.type.RecipeProvider.USER;
import static com.recipe.jamanchu.domain.model.type.TokenType.ACCESS;

import com.recipe.jamanchu.api.auth.jwt.JwtUtil;
import com.recipe.jamanchu.api.service.RecipeService;
import com.recipe.jamanchu.core.exceptions.exception.RecipeNotFoundException;
import com.recipe.jamanchu.core.exceptions.exception.UnmatchedUserException;
import com.recipe.jamanchu.domain.component.UserAccessHandler;
import com.recipe.jamanchu.domain.entity.IngredientEntity;
import com.recipe.jamanchu.domain.entity.ManualEntity;
import com.recipe.jamanchu.domain.entity.RecipeEntity;
import com.recipe.jamanchu.domain.entity.RecipeIngredientEntity;
import com.recipe.jamanchu.domain.entity.RecipeIngredientMappingEntity;
import com.recipe.jamanchu.domain.entity.ScrapedRecipeEntity;
import com.recipe.jamanchu.domain.entity.UserEntity;
import com.recipe.jamanchu.domain.model.dto.request.recipe.RecipesDTO;
import com.recipe.jamanchu.domain.model.dto.request.recipe.RecipesDeleteDTO;
import com.recipe.jamanchu.domain.model.dto.request.recipe.RecipesSearchDTO;
import com.recipe.jamanchu.domain.model.dto.request.recipe.RecipesUpdateDTO;
import com.recipe.jamanchu.domain.model.dto.response.ResultResponse;
import com.recipe.jamanchu.domain.model.dto.response.ingredients.Ingredient;
import com.recipe.jamanchu.domain.model.dto.response.recipes.RecipesInfo;
import com.recipe.jamanchu.domain.model.dto.response.recipes.RecipesManual;
import com.recipe.jamanchu.domain.model.dto.response.recipes.RecipesSummary;
import com.recipe.jamanchu.domain.model.dto.response.recipes.RecommendRecipe;
import com.recipe.jamanchu.domain.model.dto.response.recipes.RecommendRecipes;
import com.recipe.jamanchu.domain.model.type.ResultCode;
import com.recipe.jamanchu.domain.model.type.ScrapedType;
import com.recipe.jamanchu.domain.model.type.TokenType;
import com.recipe.jamanchu.domain.repository.IngredientRepository;
import com.recipe.jamanchu.domain.repository.ManualRepository;
import com.recipe.jamanchu.domain.repository.RecipeIngredientMappingRepository;
import com.recipe.jamanchu.domain.repository.RecipeIngredientRepository;
import com.recipe.jamanchu.domain.repository.RecipeRatingRepository;
import com.recipe.jamanchu.domain.repository.RecipeRepository;
import com.recipe.jamanchu.domain.repository.ScrapedRecipeRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RecipeServiceImpl implements RecipeService {

  private final RecipeRepository recipeRepository;
  private final RecipeIngredientRepository recipeIngredientRepository;
  private final ManualRepository manualRepository;
  private final ScrapedRecipeRepository scrapedRecipeRepository;
  private final RecipeRatingRepository recipeRatingRepository;
  private final RecipeIngredientMappingRepository recipeIngredientMappingRepository;
  private final IngredientRepository ingredientRepository;
  private final UserAccessHandler userAccessHandler;
  private final JwtUtil jwtUtil;

  private final Map<Long, RecommendRecipes> recommendationCache = new ConcurrentHashMap<>();

  private final Map<Long, Map<Long, Double>> recipeDifferences = new ConcurrentHashMap<>();
  private final Map<Long, Map<Long, Integer>> recipeCounts = new ConcurrentHashMap<>();

  @Override
  @Transactional
  public ResultResponse registerRecipe(HttpServletRequest request, RecipesDTO recipesDTO, String thumbnail, List<String> orderImages) {
    Long userId = jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue()));

    UserEntity user = userAccessHandler.findByUserId(userId);

    RecipeEntity recipe = RecipeEntity.builder()
        .user(user)
        .name(recipesDTO.getRecipeName())
        .level(recipesDTO.getRecipeLevel())
        .time(recipesDTO.getRecipeCookingTime())
        .thumbnail(thumbnail)
        .provider(USER)
        .build();

    recipeRepository.save(recipe);

    List<RecipeIngredientEntity> recipeIngredientEntities = new ArrayList<>();
    for (int i = 0; i < recipesDTO.getRecipeIngredients().size(); i++) {
      RecipeIngredientEntity ingredient = RecipeIngredientEntity.builder()
          .recipe(recipe)
          .name(recipesDTO.getRecipeIngredients().get(i).getIngredientName())
          .quantity(recipesDTO.getRecipeIngredients().get(i).getIngredientQuantity())
          .build();

      recipeIngredientEntities.add(ingredient);
    }

    recipeIngredientRepository.saveAll(recipeIngredientEntities);

    List<IngredientEntity> ingredientEntities = new ArrayList<>();
    List<RecipeIngredientMappingEntity> recipeIngredientMappings = new ArrayList<>();
    for (RecipeIngredientEntity recipeIngredient : recipeIngredientEntities) {
      IngredientEntity ingredient = ingredientRepository.findByIngredientName(recipeIngredient.getName())
          .orElseGet(() -> {
            IngredientEntity newIngredient = IngredientEntity.builder()
                .ingredientName(recipeIngredient.getName())
                .build();
            ingredientEntities.add(newIngredient);
            return newIngredient;
          });

      RecipeIngredientMappingEntity mapping = RecipeIngredientMappingEntity.builder()
          .recipe(recipe)
          .ingredient(ingredient)
          .build();

      recipeIngredientMappings.add(mapping);
    }

    ingredientRepository.saveAll(ingredientEntities);
    recipeIngredientMappingRepository.saveAll(recipeIngredientMappings);

    List<ManualEntity> manuals = new ArrayList<>();
    for (int i = 0; i < recipesDTO.getRecipeOrderContents().size(); i++) {
      ManualEntity manual = ManualEntity.builder()
          .recipe(recipe)
          .manualContent(recipesDTO.getRecipeOrderContents().get(i).getRecipeOrderContent())
          .manualPicture(i < orderImages.size() ? orderImages.get(i) : null)
          .build();

      manuals.add(manual);
    }

    manualRepository.saveAll(manuals);

    return ResultResponse.of(ResultCode.SUCCESS_REGISTER_RECIPE, recipe.getId());
  }

  @Override
  @Transactional
  public ResultResponse updateRecipe(HttpServletRequest request,
      RecipesUpdateDTO recipesUpdateDTO, String thumbnail, List<String> orderImages) {
    Long userId = jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue()));

    UserEntity user = userAccessHandler.findByUserId(userId);

    Long recipeId = recipesUpdateDTO.getRecipeId();

    RecipeEntity recipe = recipeRepository.findById(recipeId)
        .orElseThrow(RecipeNotFoundException::new);

    if (!Objects.equals(user.getUserId(), recipe.getUser().getUserId())) {
      throw new UnmatchedUserException();
    }

    recipe.updateRecipe(recipesUpdateDTO.getRecipeName(),
        recipesUpdateDTO.getRecipeLevel(), recipesUpdateDTO.getRecipeCookingTime(),
      thumbnail);

    // 기존 recipeId로 저장된 재료 삭제
    recipeIngredientMappingRepository.deleteAllByRecipeId(recipeId);
    recipeIngredientRepository.deleteAllByRecipeId(recipeId);

    List<RecipeIngredientEntity> recipeIngredientEntities = new ArrayList<>();
    for (int i = 0; i < recipesUpdateDTO.getRecipeIngredients().size(); i++) {
      RecipeIngredientEntity ingredient = RecipeIngredientEntity.builder()
          .recipe(recipe)
          .name(recipesUpdateDTO.getRecipeIngredients().get(i).getIngredientName())
          .quantity(recipesUpdateDTO.getRecipeIngredients().get(i).getIngredientQuantity())
          .build();

      recipeIngredientEntities.add(ingredient);
    }

    recipeIngredientRepository.saveAll(recipeIngredientEntities);

    List<IngredientEntity> ingredientEntities = new ArrayList<>();
    List<RecipeIngredientMappingEntity> recipeIngredientMappings = new ArrayList<>();
    for (RecipeIngredientEntity recipeIngredient : recipeIngredientEntities) {
      IngredientEntity ingredient = ingredientRepository.findByIngredientName(recipeIngredient.getName())
          .orElseGet(() -> {
            IngredientEntity newIngredient = IngredientEntity.builder()
                .ingredientName(recipeIngredient.getName())
                .build();
            ingredientEntities.add(newIngredient);
            return newIngredient;
          });

      RecipeIngredientMappingEntity mapping = RecipeIngredientMappingEntity.builder()
          .recipe(recipe)
          .ingredient(ingredient)
          .build();

      recipeIngredientMappings.add(mapping);
    }

    ingredientRepository.saveAll(ingredientEntities);
    recipeIngredientMappingRepository.saveAll(recipeIngredientMappings);

    manualRepository.deleteAllByRecipeId(recipeId);

    List<ManualEntity> manuals = new ArrayList<>();
    for (int i = 0; i < recipesUpdateDTO.getRecipeOrderContents().size(); i++) {
      ManualEntity manual = ManualEntity.builder()
          .recipe(recipe)
          .manualContent(recipesUpdateDTO.getRecipeOrderContents().get(i).getRecipeOrderContent())
          .manualPicture(i < orderImages.size() ? orderImages.get(i) : null)
          .build();

      manuals.add(manual);
    }

    manualRepository.saveAll(manuals);

    return ResultResponse.of(ResultCode.SUCCESS_UPDATE_RECIPE);
  }

  @Override
  @Transactional
  public ResultResponse deleteRecipe(HttpServletRequest request,
      RecipesDeleteDTO recipesDeleteDTO) {
    Long userId = jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue()));

    UserEntity user = userAccessHandler.findByUserId(userId);

    Long recipeId = recipesDeleteDTO.getRecipeId();

    RecipeEntity recipe = recipeRepository.findById(recipeId)
        .orElseThrow(RecipeNotFoundException::new);

    if (!Objects.equals(user.getUserId(), recipe.getUser().getUserId())) {
      throw new UnmatchedUserException();
    }

    recipeRepository.deleteById(recipeId);

    return ResultResponse.of(ResultCode.SUCCESS_DELETE_RECIPE);
  }

  @Override
  public ResultResponse getRecipes(HttpServletRequest request, int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
    List<Long> scrapedRecipeIds = getScrapedRecipeIds(request);

    // 만약 scrapedRecipeIds가 비어있지 않다면 SCRAPED한 레시피를 제외한 나머지 레시피 조회
    Page<RecipeEntity> recipes;
    if (!scrapedRecipeIds.isEmpty()) {
      recipes = recipeRepository.findByIdNotIn(scrapedRecipeIds, pageable);
    } else {
      // SCRAPED한 레시피가 없거나 Token이 없으면 모든 레시피를 조회
      recipes = recipeRepository.findAll(pageable);
    }

    // 레시피가 없는 경우 예외 처리
    if (recipes.isEmpty()) {
      throw new RecipeNotFoundException();
    }

    List<RecipesSummary> recipesSummaries = convertToRecipesSummary(recipes);

    // 결과 반환
    return ResultResponse.of(ResultCode.SUCCESS_RETRIEVE_ALL_RECIPES, recipesSummaries);
  }

  @Override
  public ResultResponse searchRecipes(HttpServletRequest request, RecipesSearchDTO recipesSearchDTO,
      int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
    List<Long> scrapedRecipeIds = getScrapedRecipeIds(request);

    Page<RecipeEntity> recipes = recipeRepository.searchAndRecipesQueryDSL(
        recipesSearchDTO, scrapedRecipeIds, pageable);

    if (recipes.isEmpty()) {
      recipes = recipeRepository.searchOrRecipesQueryDSL(
          recipesSearchDTO, scrapedRecipeIds, pageable);
    }

    if (recipes.isEmpty()) {
      throw new RecipeNotFoundException();
    }

    List<RecipesSummary> recipesSummaries = convertToRecipesSummary(recipes);

    return ResultResponse.of(ResultCode.SUCCESS_RETRIEVE_RECIPES, recipesSummaries);
  }

  @Override
  public ResultResponse getRecipeDetail(Long recipeId) {
    RecipeEntity recipe = recipeRepository.findById(recipeId)
        .orElseThrow(RecipeNotFoundException::new);

    List<Ingredient> ingredients = recipe.getIngredients().stream()
        .map(ingredient -> new Ingredient(
            ingredient.getName(),
            ingredient.getQuantity()
        ))
        .toList();

    List<RecipesManual> recipesManuals = recipe.getManuals().stream()
        .map(manual -> new RecipesManual(
            manual.getManualContent(),
            manual.getManualPicture()
        ))
        .toList();

    RecipesInfo recipesInfo = new RecipesInfo(
        recipe.getId(),
        recipe.getUser().getNickname(),
        recipe.getName(),
        recipe.getLevel(),
        recipe.getTime(),
        recipe.getThumbnail(),
        ingredients,
        recipesManuals,
        Optional.ofNullable(recipeRatingRepository.findAverageRatingByRecipeId(recipe.getId()))
            .orElse(0.0)
    );

    return ResultResponse.of(ResultCode.SUCCESS_RETRIEVE_RECIPES_DETAILS, recipesInfo);
  }

  @Override
  public ResultResponse getRecipesByRating(HttpServletRequest request, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    List<Long> scrapedRecipeIds = getScrapedRecipeIds(request);

    // 만약 scrapedRecipeIds가 비어있지 않다면 SCRAPED한 레시피를 제외한 나머지 레시피를 평점순으로 조회
    Page<RecipeEntity> recipes;
    if (!scrapedRecipeIds.isEmpty()) {
      recipes = recipeRepository.findByIdNotInOrderByRating(scrapedRecipeIds, pageable);
    } else {
      // SCRAPED한 레시피가 없거나 Token이 없으면 모든 레시피를 평점순으로 조회
      recipes = recipeRepository.findAllOrderByRating(pageable);
    }

    if (recipes.isEmpty()) {
      throw new RecipeNotFoundException();
    }

    List<RecipesSummary> recipesSummaries = convertToRecipesSummary(recipes);

    return ResultResponse.of(ResultCode.SUCCESS_RETRIEVE_ALL_RECIPES_BY_RATING, recipesSummaries);
  }

  @Override
  public ResultResponse scrapedRecipe(HttpServletRequest request, Long recipeId) {
    Long userId = jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue()));

    UserEntity user = userAccessHandler.findByUserId(userId);

    RecipeEntity recipe = recipeRepository.findById(recipeId)
        .orElseThrow(RecipeNotFoundException::new);

    ScrapedRecipeEntity scrapedRecipe = scrapedRecipeRepository.findByUserAndRecipe(user, recipe);

    if (scrapedRecipe != null) {
      scrapedRecipe.updateScrapedType(scrapedRecipe.getScrapedType() == ScrapedType.SCRAPED
          ? ScrapedType.CANCELED
          : ScrapedType.SCRAPED);
    } else {
      scrapedRecipe = ScrapedRecipeEntity.builder()
          .user(user)
          .recipe(recipe)
          .scrapedType(ScrapedType.SCRAPED)
          .build();
    }

    scrapedRecipeRepository.save(scrapedRecipe);

    return ResultResponse.of(
        scrapedRecipe.getScrapedType() == ScrapedType.SCRAPED
            ? ResultCode.SUCCESS_SCRAPED_RECIPE
            : ResultCode.SUCCESS_CANCELED_SCRAP_RECIPE, scrapedRecipe.getScrapedType());
  }


  @Override
  public ResultResponse getRecommendRecipes(HttpServletRequest request) {

    // 유저 아이디 가져오기
    Long userId = jwtUtil.getUserId(request.getHeader(ACCESS.getValue()));

    // 유저 확인
    UserEntity user = userAccessHandler.findByUserId(userId);

    // 레시피 평가 항목이 있는지 조회
    if (!recipeRatingRepository.existsByUser(user)) {
      // 분기) 레시피 평가 항목이 없을 경우 -> 기존 레시피에서 평균 평점이 제일 높은 레시피 추천
      return ResultResponse.of(ResultCode.SUCCESS_RETRIEVE_RECOMMEND_RECIPES,
          getRecommendRecipesWhenNoRating());
    } else {
      // 분기) 레시피 평가 항목이 있는 경우 -> 유저의 평가 항목을 기반으로 레시피 추천
      return ResultResponse.of(ResultCode.SUCCESS_RETRIEVE_RECOMMEND_RECIPES,
          getRecommendRecipesWhenRating(user.getUserId()));
    }
  }

  /*
   * 레시피 평가 항목이 없을 경우 -> 기존 레시피에서 평균 평점이 제일 높은 레시피 추천
   */
  private RecommendRecipes getRecommendRecipesWhenNoRating() {
    return RecommendRecipes.of(
        recipeRatingRepository.findThreePopularRecipe()
            .stream()
            .map(recipe -> {
              UserEntity user = recipe.getUser();
              return RecommendRecipe.of(recipe, user);
            })
            .collect(Collectors.toList())
    );
  }

  /*
   * 레시피 평가 항목이 있는 경우 -> 유저의 평가 항목을 기반으로 레시피 추천
   */
  private RecommendRecipes getRecommendRecipesWhenRating(Long userId) {

    if (recommendationCache.get(userId).getRecipes().isEmpty()
        || recipeRatingRepository.findAll().size() < 30) {
      return getRecommendRecipesWhenNoRating();
    }
    return recommendationCache.getOrDefault(userId, RecommendRecipes.empty());
  }



  // 유저의 scrapedRecipeIds를 가져오는 메서드
  private List<Long> getScrapedRecipeIds(HttpServletRequest request) {
    Long userId;
    List<Long> scrapedRecipeIds = Collections.emptyList();
    String token = request.getHeader(TokenType.ACCESS.getValue());
    if (token != null) {
      userId = jwtUtil.getUserId(token);
    } else {
      return scrapedRecipeIds;
    }

    if (userId != null) {
      scrapedRecipeIds = scrapedRecipeRepository.findRecipeIdsByUserIdAndScrapedType(userId,
          ScrapedType.SCRAPED);
    }

    return scrapedRecipeIds;
  }

  // RecipeEntity 리스트를 RecipesSummary로 변환하는 메서드
  private List<RecipesSummary> convertToRecipesSummary(Page<RecipeEntity> recipes) {
    return recipes.stream().map(
        recipeEntity -> new RecipesSummary(
            recipeEntity.getId(),
            recipeEntity.getName(),
            recipeEntity.getUser().getNickname(),
            recipeEntity.getLevel(),
            recipeEntity.getTime(),
            recipeEntity.getThumbnail(),
            Optional.ofNullable(
                    recipeRatingRepository.findAverageRatingByRecipeId(recipeEntity.getId()))
                .orElse(0.0)
        )
    ).toList();
  }
}
