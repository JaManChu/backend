package com.recipe.jamanchu.domain.repository;

import com.recipe.jamanchu.domain.entity.RecipeEntity;
import com.recipe.jamanchu.domain.entity.RecipeRatingEntity;
import com.recipe.jamanchu.domain.entity.UserEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRatingRepository extends JpaRepository<RecipeRatingEntity, Long> {

  @Query("SELECT AVG(r.rrRating) FROM RecipeRatingEntity r WHERE r.recipe.rcpId = :recipeId")
  Double findAverageRatingByRecipeRcpId(@Param("recipeId") Long recipeId);

  boolean existsByUser(UserEntity user);

  @Query("SELECT rr.recipe FROM RecipeRatingEntity rr GROUP BY rr.recipe.rcpId ORDER BY AVG(rr.rrRating) DESC LIMIT 3")
  List<RecipeEntity> findThreePopularRecipe();

  List<RecipeRatingEntity> findByUser(UserEntity user);

  void deleteAllByUser(UserEntity user);

  @Query("SELECT rr FROM RecipeRatingEntity rr WHERE rr.rrRating >= 1.0")
  List<RecipeRatingEntity> findAllWhereRatingOverOne();
}
