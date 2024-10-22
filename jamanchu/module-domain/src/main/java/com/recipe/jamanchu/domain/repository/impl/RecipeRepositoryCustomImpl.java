package com.recipe.jamanchu.repository.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.recipe.jamanchu.entity.QRecipeEntity;
import com.recipe.jamanchu.entity.QRecipeIngredientEntity;
import com.recipe.jamanchu.entity.QScrapedRecipeEntity;
import com.recipe.jamanchu.entity.RecipeEntity;
import com.recipe.jamanchu.model.type.CookingTimeType;
import com.recipe.jamanchu.model.type.LevelType;
import com.recipe.jamanchu.model.type.ScrapedType;
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
  public Page<RecipeEntity> searchAndRecipesQueryDSL(LevelType level, CookingTimeType cookingTime,
      List<String> ingredients, Long ingredientCount, Pageable pageable, List<Long> scrapedRecipeIds) {
    QRecipeEntity recipe = QRecipeEntity.recipeEntity;
    QRecipeIngredientEntity recipeIngredient = QRecipeIngredientEntity.recipeIngredientEntity;

    // 기본 쿼리
    JPQLQuery<RecipeEntity> query = queryFactory.selectFrom(recipe)
        .join(recipe.ingredients, recipeIngredient);

    // 조건 중 하나라도 있어야 하므로, 기본 조건을 생성합니다.
    BooleanExpression combinedCondition = Expressions.asBoolean(true).isFalse();

    // 재료 조건 추가 (모든 재료를 포함하는 조건)
    if (ingredients != null && !ingredients.isEmpty()) {
      for (String ingredient : ingredients) {
        combinedCondition = combinedCondition.or(
            recipeIngredient.name.likeIgnoreCase("%" + ingredient + "%"));
      }

      // 모든 재료가 포함된 레시피를 찾아야 하므로, 각 재료를 포함하는 레시피 ID를 구합니다.
      for (String ingredient : ingredients) {
        List<Long> ingredientRecipeIds = queryFactory.select(recipe.id)
            .from(recipe)
            .join(recipe.ingredients, recipeIngredient)
            .where(recipeIngredient.name.likeIgnoreCase("%" + ingredient + "%"))
            .fetch();

        combinedCondition = combinedCondition.and(recipe.id.in(ingredientRecipeIds));
      }
    }

    // 난이도 조건 추가
    if (level != null) {
      combinedCondition = combinedCondition.and(recipe.level.eq(level));
    }

    // 요리 시간 조건 추가
    if (cookingTime != null) {
      combinedCondition = combinedCondition.and(recipe.time.eq(cookingTime));
    }

    // 스크랩된 레시피 제외 조건 추가  --> 테스트 필요
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