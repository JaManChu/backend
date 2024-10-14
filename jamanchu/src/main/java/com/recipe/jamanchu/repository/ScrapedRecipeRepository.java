package com.recipe.jamanchu.repository;

import com.recipe.jamanchu.entity.RecipeEntity;
import com.recipe.jamanchu.entity.ScrapedRecipeEntity;
import com.recipe.jamanchu.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScrapedRecipeRepository extends JpaRepository<ScrapedRecipeEntity, Long> {

  ScrapedRecipeEntity findByUserAndRecipe(UserEntity user, RecipeEntity recipe);
}
