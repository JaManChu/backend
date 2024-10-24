package com.recipe.jamanchu.domain.repository;

import com.recipe.jamanchu.domain.entity.RecipeIngredientMappingEntity;
import com.recipe.jamanchu.core.util.RecipeIngredientMappingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeIngredientMappingRepository extends JpaRepository<RecipeIngredientMappingEntity, RecipeIngredientMappingId> {

  @Modifying
  @Query("DELETE FROM RecipeIngredientMappingEntity r WHERE r.recipe.id = :recipeId")
  void deleteAllByRecipeId(@Param("recipeId") Long recipeId);
}
