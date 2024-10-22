package com.recipe.jamanchu.domain.repository;

import com.recipe.jamanchu.domain.entity.IngredientRatingEntity;
import com.recipe.jamanchu.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRatingRepository extends JpaRepository<IngredientRatingEntity, Long> {

  void deleteAllByUser(UserEntity user);
}
