package com.recipe.jamanchu.domain.repository;

import com.recipe.jamanchu.domain.entity.RecipeEntity;
import com.recipe.jamanchu.domain.entity.ScrapedRecipeEntity;
import com.recipe.jamanchu.domain.entity.UserEntity;
import com.recipe.jamanchu.domain.model.type.ScrapedType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ScrapedRecipeRepository extends JpaRepository<ScrapedRecipeEntity, Long> {

  ScrapedRecipeEntity findByUserAndRecipe(UserEntity user, RecipeEntity recipe);

  @Query("SELECT sr.recipe.id FROM ScrapedRecipeEntity sr WHERE sr.user.userId = :userId AND sr.scrapedType = :scrapedType")
  List<Long> findRecipeIdsByUserIdAndScrapedType(Long userId, ScrapedType scrapedType);

  void deleteAllByUser(UserEntity user);
}
