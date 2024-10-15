package com.recipe.jamanchu.service.impl;

import static com.recipe.jamanchu.model.type.RecipeProvider.*;

import com.recipe.jamanchu.auth.jwt.JwtUtil;
import com.recipe.jamanchu.component.UserAccessHandler;
import com.recipe.jamanchu.entity.IngredientEntity;
import com.recipe.jamanchu.entity.ManualEntity;
import com.recipe.jamanchu.entity.RecipeEntity;
import com.recipe.jamanchu.entity.RecipeIngredientMappingEntity;
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
import com.recipe.jamanchu.model.type.ResultCode;
import com.recipe.jamanchu.model.type.ScrapedType;
import com.recipe.jamanchu.model.type.TokenType;
import com.recipe.jamanchu.repository.IngredientRepository;
import com.recipe.jamanchu.repository.ManualRepository;
import com.recipe.jamanchu.repository.RecipeIngredientMappingRepository;
import com.recipe.jamanchu.repository.RecipeRatingRepository;
import com.recipe.jamanchu.repository.RecipeRepository;
import com.recipe.jamanchu.repository.ScrapedRecipeRepository;
import com.recipe.jamanchu.service.RecipeService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
  private final IngredientRepository ingredientRepository;
  private final ManualRepository manualRepository;
  private final ScrapedRecipeRepository scrapedRecipeRepository;
  private final RecipeRatingRepository recipeRatingRepository;
  private final RecipeIngredientMappingRepository recipeIngredientMappingRepository;
  private final UserAccessHandler userAccessHandler;
  private final JwtUtil jwtUtil;

  @Override
  @Transactional
  public ResultResponse registerRecipe(HttpServletRequest request, RecipesDTO recipesDTO) {
    Long userId = jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue()));

    UserEntity user = userAccessHandler.findByUserId(userId);

    RecipeEntity recipe = RecipeEntity.builder()
        .user(user)
        .name(recipesDTO.getRecipeName())
        .level(recipesDTO.getRecipeLevel())
        .time(recipesDTO.getRecipeCookingTime())
        .thumbnail(String.valueOf(recipesDTO.getRecipeThumbnail()))
        .provider(USER)
        .build();

    recipeRepository.save(recipe);

    List<IngredientEntity> ingredients = new ArrayList<>();
    for (int i = 0; i < recipesDTO.getRecipeIngredients().size(); i++) {
      IngredientEntity ingredient = IngredientEntity.builder()
          .recipe(recipe)
          .name(recipesDTO.getRecipeIngredients().get(i).getIngredientName())
          .quantity(recipesDTO.getRecipeIngredients().get(i).getIngredientQuantity())
          .build();

      ingredients.add(ingredient);
    }

    ingredientRepository.saveAll(ingredients);

    List<RecipeIngredientMappingEntity> recipeIngredientMappings = new ArrayList<>();
    for (IngredientEntity ingredient : ingredients) {
      RecipeIngredientMappingEntity mapping = RecipeIngredientMappingEntity.builder()
          .recipe(recipe)
          .ingredient(ingredient)
          .build();

      recipeIngredientMappings.add(mapping);
    }

    recipeIngredientMappingRepository.saveAll(recipeIngredientMappings);

    List<ManualEntity> manuals = new ArrayList<>();
    for (int i = 0; i < recipesDTO.getRecipeOrderContents().size(); i++) {
      ManualEntity manual = ManualEntity.builder()
          .recipe(recipe)
          .manualContent(recipesDTO.getRecipeOrderContents().get(i).getRecipeOrderContent())
          .manualPicture(recipesDTO.getRecipeOrderContents().get(i).getRecipeOrderImage())
          .build();

      manuals.add(manual);
    }

    manualRepository.saveAll(manuals);

    return ResultResponse.of(ResultCode.SUCCESS_REGISTER_RECIPE, recipe.getId());
  }

  @Override
  @Transactional
  public ResultResponse updateRecipe(HttpServletRequest request,
      RecipesUpdateDTO recipesUpdateDTO) {
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
        String.valueOf(recipesUpdateDTO.getRecipeThumbnail()));

    // 기존 recipeId로 저장된 재료 삭제
    recipeIngredientMappingRepository.deleteAllByRecipeId(recipeId);
    ingredientRepository.deleteAllByRecipeId(recipeId);

    List<IngredientEntity> ingredients = new ArrayList<>();
    for (int i = 0; i < recipesUpdateDTO.getRecipeIngredients().size(); i++) {
      IngredientEntity ingredient = IngredientEntity.builder()
          .recipe(recipe)
          .name(recipesUpdateDTO.getRecipeIngredients().get(i).getIngredientName())
          .quantity(recipesUpdateDTO.getRecipeIngredients().get(i).getIngredientQuantity())
          .build();

      ingredients.add(ingredient);
    }

    ingredientRepository.saveAll(ingredients);

    List<RecipeIngredientMappingEntity> recipeIngredientMappings = new ArrayList<>();
    for (IngredientEntity ingredient : ingredients) {
      RecipeIngredientMappingEntity mapping = RecipeIngredientMappingEntity.builder()
          .recipe(recipe)
          .ingredient(ingredient)
          .build();

      recipeIngredientMappings.add(mapping);
    }

    recipeIngredientMappingRepository.saveAll(recipeIngredientMappings);

    manualRepository.deleteAllByRecipeId(recipeId);

    List<ManualEntity> manuals = new ArrayList<>();
    for (int i = 0; i < recipesUpdateDTO.getRecipeOrderContents().size(); i++) {
      ManualEntity manual = ManualEntity.builder()
          .recipe(recipe)
          .manualContent(recipesUpdateDTO.getRecipeOrderContents().get(i).getRecipeOrderContent())
          .manualPicture(recipesUpdateDTO.getRecipeOrderContents().get(i).getRecipeOrderImage())
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
  public ResultResponse getRecipes(int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

    Page<RecipeEntity> recipes = recipeRepository.findAll(pageable);

    if (recipes.isEmpty()) {
      throw new RecipeNotFoundException();
    }

    List<RecipesSummary> recipesSummaries = recipes.stream().map(
        recipeEntity -> new RecipesSummary(
        recipeEntity.getId(),
        recipeEntity.getName(),
        recipeEntity.getUser().getNickname(),
        recipeEntity.getLevel(),
        recipeEntity.getTime(),
        recipeEntity.getThumbnail(),
        Optional.ofNullable(recipeRatingRepository.findAverageRatingByRecipeId(recipeEntity.getId()))
            .orElse(0.0)
    )).toList();

    return ResultResponse.of(ResultCode.SUCCESS_RETRIEVE_ALL_RECIPES, recipesSummaries);
  }

  @Override
  public ResultResponse searchRecipes(RecipesSearchDTO recipesSearchDTO, int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

    Page<RecipeEntity> recipes = recipeRepository.searchAndRecipes(
        recipesSearchDTO.getRecipeLevel(),
        recipesSearchDTO.getRecipeCookingTime(),
        recipesSearchDTO.getIngredients(),
        (long) recipesSearchDTO.getIngredients().size(),
        pageable
    );

    if (recipes.isEmpty()) {
      recipes = recipeRepository.searchOrRecipes(
          recipesSearchDTO.getRecipeLevel(),
          recipesSearchDTO.getRecipeCookingTime(),
          recipesSearchDTO.getIngredients(),
          pageable);
    }

    if (recipes.isEmpty()) {
      throw new RecipeNotFoundException();
    }

    List<RecipesSummary> recipesSummaries = recipes.stream().map(
        recipeEntity -> new RecipesSummary(
            recipeEntity.getId(),
            recipeEntity.getName(),
            recipeEntity.getUser().getNickname(),
            recipeEntity.getLevel(),
            recipeEntity.getTime(),
            recipeEntity.getThumbnail(),
            Optional.ofNullable(recipeRatingRepository.findAverageRatingByRecipeId(recipeEntity.getId()))
                .orElse(0.0)
        )).toList();

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
  public ResultResponse getRecipesByRating(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);

    Page<RecipeEntity> recipes = recipeRepository.findAllOrderByRating(pageable);

    if (recipes.isEmpty()) {
      throw new RecipeNotFoundException();
    }

    List<RecipesSummary> recipesSummaries = recipes.stream().map(
        recipeEntity -> new RecipesSummary(
            recipeEntity.getId(),
            recipeEntity.getName(),
            recipeEntity.getUser().getNickname(),
            recipeEntity.getLevel(),
            recipeEntity.getTime(),
            recipeEntity.getThumbnail(),
            Optional.ofNullable(recipeRatingRepository.findAverageRatingByRecipeId(recipeEntity.getId()))
                .orElse(0.0)
        )).toList();

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
}
