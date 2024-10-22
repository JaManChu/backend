package com.recipe.jamanchu.repository.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.recipe.jamanchu.entity.QRecipeEntity;
import com.recipe.jamanchu.entity.QRecipeIngredientEntity;
import com.recipe.jamanchu.entity.RecipeEntity;
import com.recipe.jamanchu.model.dto.request.recipe.RecipesSearchDTO;
import com.recipe.jamanchu.repository.RecipeRepositoryCustom;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RecipeRepositoryCustomImpl implements RecipeRepositoryCustom {

  private final JPAQueryFactory queryFactory;
  @Override
  public Page<RecipeEntity> searchAndRecipesQueryDSL(RecipesSearchDTO recipesSearchDTO,
      List<Long> scrapedRecipeIds, Pageable pageable) {
    QRecipeEntity recipe = QRecipeEntity.recipeEntity;
    QRecipeIngredientEntity recipeIngredient = QRecipeIngredientEntity.recipeIngredientEntity;

    // 기본 쿼리
    JPQLQuery<RecipeEntity> query = queryFactory.selectFrom(recipe)
        .distinct()
        .join(recipe.ingredients, recipeIngredient);

    // 조건 중 하나라도 있어야 하므로, 기본 조건을 생성합니다.
    BooleanExpression combinedCondition = Expressions.asBoolean(true).isFalse();

    // 재료 조건 추가 (모든 재료를 포함하는 조건)
    if (recipesSearchDTO.getIngredients() != null && !recipesSearchDTO.getIngredients().isEmpty()) {
      for (String ingredient : recipesSearchDTO.getIngredients()) {
        combinedCondition = combinedCondition.or(
            recipeIngredient.name.likeIgnoreCase("%" + ingredient + "%"));
      }

      // 모든 재료가 포함된 레시피를 찾아야 하므로, 각 재료를 포함하는 레시피 ID를 구합니다.
      for (String ingredient : recipesSearchDTO.getIngredients()) {
        List<Long> ingredientRecipeIds = queryFactory.select(recipe.id)
            .from(recipe)
            .join(recipe.ingredients, recipeIngredient)
            .where(recipeIngredient.name.likeIgnoreCase("%" + ingredient + "%"))
            .fetch();

        combinedCondition = combinedCondition.and(recipe.id.in(ingredientRecipeIds));
      }
    }

    // 난이도 조건 추가
    if (recipesSearchDTO.getRecipeLevel() != null) {
      combinedCondition = combinedCondition.and(recipe.level.eq(recipesSearchDTO.getRecipeLevel()));
    }

    // 요리 시간 조건 추가
    if (recipesSearchDTO.getRecipeCookingTime() != null) {
      combinedCondition = combinedCondition.and(recipe.time.eq(recipesSearchDTO.getRecipeCookingTime()));
    }

    // 스크랩된 레시피 제외 조건 추가
    if (!scrapedRecipeIds.isEmpty()) {
      combinedCondition = combinedCondition.and(recipe.id.notIn(scrapedRecipeIds));
    }

    // 모든 조건을 쿼리에 추가
    query.where(combinedCondition);

    // 페이징 처리
    long total = query.fetchCount();
    List<RecipeEntity> results = query.offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    return new PageImpl<>(results, pageable, total);
  }

  @Override
  public Page<RecipeEntity> searchOrRecipesQueryDSL(RecipesSearchDTO recipesSearchDTO,
      List<Long> scrapedRecipeIds, Pageable pageable) {
    QRecipeEntity recipe = QRecipeEntity.recipeEntity;
    QRecipeIngredientEntity recipeIngredient = QRecipeIngredientEntity.recipeIngredientEntity;

    // 기본 쿼리
    JPQLQuery<RecipeEntity> query = queryFactory.selectFrom(recipe)
        .distinct()
        .join(recipe.ingredients, recipeIngredient);

    // 조건 중 하나라도 있어야 하므로, 기본 조건을 생성합니다.
    BooleanExpression combinedCondition = Expressions.asBoolean(true).isFalse();

    // 재료 조건 추가 (모든 재료를 포함하는 조건)
    if (recipesSearchDTO.getIngredients() != null && !recipesSearchDTO.getIngredients().isEmpty()) {
      BooleanExpression ingredientCondition = null;

      for (String ingredient : recipesSearchDTO.getIngredients()) {
        BooleanExpression singleIngredientCondition = recipeIngredient.name.likeIgnoreCase("%" + ingredient + "%");
        ingredientCondition = (ingredientCondition == null) ? singleIngredientCondition : ingredientCondition.or(singleIngredientCondition);
      }

      combinedCondition = combinedCondition.or(ingredientCondition);
    }

    // 난이도 조건 추가
    if (recipesSearchDTO.getRecipeLevel() != null) {
      combinedCondition = combinedCondition.or(recipe.level.eq(recipesSearchDTO.getRecipeLevel()));
    }

    // 요리 시간 조건 추가
    if (recipesSearchDTO.getRecipeCookingTime() != null) {
      combinedCondition = combinedCondition.or(recipe.time.eq(recipesSearchDTO.getRecipeCookingTime()));
    }

    // 스크랩된 레시피 제외 조건 추가
    if (!scrapedRecipeIds.isEmpty()) {
      combinedCondition = combinedCondition.and(recipe.id.notIn(scrapedRecipeIds));
    }

    // 모든 조건을 쿼리에 추가
    query.where(combinedCondition);

    // 페이징 처리
    long total = query.fetchCount();
    List<RecipeEntity> results = query.offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    return new PageImpl<>(results, pageable, total);
  }
}