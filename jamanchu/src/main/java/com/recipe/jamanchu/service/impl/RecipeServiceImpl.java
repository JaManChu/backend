package com.recipe.jamanchu.service.impl;

import static com.recipe.jamanchu.model.type.RecipeProvider.*;

import com.recipe.jamanchu.auth.jwt.JwtUtil;
import com.recipe.jamanchu.component.UserAccessHandler;
import com.recipe.jamanchu.entity.IngredientEntity;
import com.recipe.jamanchu.entity.ManualEntity;
import com.recipe.jamanchu.entity.RecipeEntity;
import com.recipe.jamanchu.entity.UserEntity;
import com.recipe.jamanchu.exceptions.exception.RecipeNotFoundException;
import com.recipe.jamanchu.exceptions.exception.UnmatchedUserException;
import com.recipe.jamanchu.model.dto.request.recipe.RecipesDTO;
import com.recipe.jamanchu.model.dto.request.recipe.RecipesDeleteDTO;
import com.recipe.jamanchu.model.dto.request.recipe.RecipesSearchDTO;
import com.recipe.jamanchu.model.dto.request.recipe.RecipesUpdateDTO;
import com.recipe.jamanchu.model.dto.response.ResultResponse;
import com.recipe.jamanchu.model.dto.response.recipes.RecipesSummary;
import com.recipe.jamanchu.model.type.ResultCode;
import com.recipe.jamanchu.repository.IngredientRepository;
import com.recipe.jamanchu.repository.ManualRepository;
import com.recipe.jamanchu.repository.RecipeRepository;
import com.recipe.jamanchu.service.RecipeService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
  private final UserAccessHandler userAccessHandler;
  private final JwtUtil jwtUtil;

  @Override
  @Transactional
  public ResultResponse registerRecipe(HttpServletRequest request, RecipesDTO recipesDTO) {

    Long userId = jwtUtil.getUserId(request.getHeader("access-token"));
    UserEntity user = userAccessHandler.findByUserId(userId);

    RecipeEntity recipe = RecipeEntity.builder()
        .user(user)
        .name(recipesDTO.getRecipeName())
        .level(recipesDTO.getLevel())
        .time(recipesDTO.getCookingTime())
        .thumbnail(recipesDTO.getRecipeImage())
        .provider(USER)
        .build();

    recipeRepository.save(recipe);

    List<IngredientEntity> ingredients = new ArrayList<>();
    for (int i = 0; i < recipesDTO.getIngredients().size(); i++) {
      IngredientEntity ingredient = IngredientEntity.builder()
          .recipe(recipe)
          .name(recipesDTO.getIngredients().get(i).getName())
          .quantity(recipesDTO.getIngredients().get(i).getQuantity())
          .build();

      ingredients.add(ingredient);
    }

    ingredientRepository.saveAll(ingredients);

    List<ManualEntity> manuals = new ArrayList<>();
    for (int i = 0; i < recipesDTO.getManuals().size(); i++) {
      ManualEntity manual = ManualEntity.builder()
          .recipe(recipe)
          .manualContent(recipesDTO.getManuals().get(i).getManualContent())
          .manualPicture(recipesDTO.getManuals().get(i).getManualPicture())
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
    Long userId = jwtUtil.getUserId(request.getHeader("access-token"));

    UserEntity user = userAccessHandler.findByUserId(userId);

    Long recipeId = recipesUpdateDTO.getRecipeId();

    RecipeEntity recipe = recipeRepository.findById(recipeId)
        .orElseThrow(RecipeNotFoundException::new);

    if (!Objects.equals(user.getUserId(), recipe.getUser().getUserId())) {
      throw new UnmatchedUserException();
    }

    recipe.updateRecipe(recipesUpdateDTO.getRecipeName(),
        recipesUpdateDTO.getLevel(), recipesUpdateDTO.getCookingTime(),
        recipesUpdateDTO.getRecipeImage());

    // 기존 recipeId로 저장된 재료 확인
    ingredientRepository.findAllByRecipeId(recipeId)
        .orElseThrow(RecipeNotFoundException::new);
    // 기존 recipeId로 저장된 재료 리스트 삭제
    ingredientRepository.deleteAllByRecipeId(recipeId);

    List<IngredientEntity> ingredients = new ArrayList<>();
    for (int i = 0; i < recipesUpdateDTO.getIngredients().size(); i++) {
      IngredientEntity ingredient = IngredientEntity.builder()
          .recipe(recipe)
          .name(recipesUpdateDTO.getIngredients().get(i).getName())
          .quantity(recipesUpdateDTO.getIngredients().get(i).getQuantity())
          .build();

      ingredients.add(ingredient);
    }

    ingredientRepository.saveAll(ingredients);

    manualRepository.findAllByRecipeId(recipeId)
        .orElseThrow(RecipeNotFoundException::new);

    manualRepository.deleteAllByRecipeId(recipeId);

    List<ManualEntity> manuals = new ArrayList<>();
    for (int i = 0; i < recipesUpdateDTO.getManuals().size(); i++) {
      ManualEntity manual = ManualEntity.builder()
          .recipe(recipe)
          .manualContent(recipesUpdateDTO.getManuals().get(i).getManualContent())
          .manualPicture(recipesUpdateDTO.getManuals().get(i).getManualPicture())
          .build();

      manuals.add(manual);
    }

    manualRepository.saveAll(manuals);

    return ResultResponse.of(ResultCode.SUCCESS_UPDATE_RECIPE);
  }

  @Override
  public ResultResponse deleteRecipe(HttpServletRequest request,
      RecipesDeleteDTO recipesDeleteDTO) {
    Long userId = jwtUtil.getUserId(request.getHeader("access-token"));

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
        recipeEntity.getThumbnail()
    )).toList();

    return ResultResponse.of(ResultCode.SUCCESS_RETRIEVE_ALL_RECIPES, recipesSummaries);
  }

  @Override
  public ResultResponse searchRecipes(RecipesSearchDTO recipesSearchDTO, int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

    Page<RecipeEntity> recipes = recipeRepository.searchAndRecipes(
        recipesSearchDTO.getLevel(),
        recipesSearchDTO.getCookingTime(),
        recipesSearchDTO.getIngredients(),
        (long) recipesSearchDTO.getIngredients().size(),
        pageable
    );

    if (recipes.isEmpty()) {
      recipes = recipeRepository.searchOrRecipes(
          recipesSearchDTO.getLevel(),
          recipesSearchDTO.getCookingTime(),
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
            recipeEntity.getThumbnail()
        )).toList();

    return ResultResponse.of(ResultCode.SUCCESS_RETRIEVE_RECIPES, recipesSummaries);
  }
}
