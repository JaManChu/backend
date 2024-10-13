package com.recipe.jamanchu.repository;

import com.recipe.jamanchu.entity.RecipeRatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRatingRepository extends JpaRepository<RecipeRatingEntity, Long> {

  @Query("SELECT AVG(r.rating) FROM RecipeRatingEntity r WHERE r.recipe.id = :recipeId")
  Double findAverageRatingByRecipeId(@Param("recipeId") Long recipeId);
}
