package com.recipe.jamanchu.repository;

import com.recipe.jamanchu.entity.IngredientRatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRatingRepository extends JpaRepository<IngredientRatingEntity, Long> {

}
