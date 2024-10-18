package com.recipe.jamanchu.repository;

import com.recipe.jamanchu.entity.ManualEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ManualRepository extends JpaRepository<ManualEntity, Long> {

  @Modifying
  @Query("DELETE FROM ManualEntity m WHERE m.recipe.id = :recipeId")
  void deleteAllByRecipeId(@Param("recipeId") Long recipeId);
}
