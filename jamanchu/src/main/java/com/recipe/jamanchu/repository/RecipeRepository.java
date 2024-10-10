package com.recipe.jamanchu.repository;

import com.recipe.jamanchu.entity.RecipeEntity;
import com.recipe.jamanchu.model.type.CookingTimeType;
import com.recipe.jamanchu.model.type.LevelType;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends JpaRepository<RecipeEntity, Long> {

  @Query(value = "SELECT r FROM RecipeEntity r JOIN r.ingredients ri " +
      "WHERE (:ingredients IS NULL OR ri.name IN :ingredients) " +
      "AND (:cookingTime IS NULL OR r.time = :cookingTime) " +
      "AND (:level IS NULL OR r.level = :level) " +
      "GROUP BY r.id " +
      "HAVING COUNT(ri.name) = :ingredientCount")
  Page<RecipeEntity> searchAndRecipes(@Param("level") LevelType level,
      @Param("cookingTime") CookingTimeType cookingTime,
      @Param("ingredients") List<String> ingredients,
      @Param("ingredientCount") Long ingredientCount,
      Pageable pageable);

  @Query(value = "SELECT r FROM RecipeEntity r JOIN r.ingredients ri " +
      "WHERE (:level IS NULL OR r.level = :level) " +
      "OR (:cookingTime IS NULL OR r.time = :cookingTime) " +
      "OR (:ingredients IS NULL OR ri.name IN :ingredients) " +
      "GROUP BY r.id")
  Page<RecipeEntity> searchOrRecipes(@Param("level") LevelType level,
      @Param("cookingTime") CookingTimeType cookingTime,
      @Param("ingredients") List<String> ingredients,
      Pageable pageable);

  @Query("SELECT r FROM RecipeEntity r " +
      "LEFT JOIN r.rating rat " +
      "GROUP BY r.id " +
      "ORDER BY AVG(rat.rating) DESC")
  Page<RecipeEntity> findAllOrderByRating(Pageable pageable);
}
