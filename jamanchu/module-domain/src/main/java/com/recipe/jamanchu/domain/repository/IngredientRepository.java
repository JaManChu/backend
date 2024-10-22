package com.recipe.jamanchu.domain.repository;

import com.recipe.jamanchu.domain.entity.IngredientEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<IngredientEntity, Long> {

  Optional<IngredientEntity> findByIngredientName(String ingredientName);
}
