package com.recipe.jamanchu.domain.repository;

import com.recipe.jamanchu.domain.entity.RecipeIngredientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredientEntity, Long> {

  @Modifying
  @Query("DELETE FROM RecipeIngredientEntity i WHERE i.recipe.id = :recipeId")
  void deleteAllByRecipeId(@Param("recipeId") Long recipeId);
}
