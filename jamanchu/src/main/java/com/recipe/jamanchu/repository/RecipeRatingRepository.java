package com.recipe.jamanchu.repository;

import com.recipe.jamanchu.entity.RecipeRatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRatingRepository extends JpaRepository<RecipeRatingEntity, Long> {

}
