package com.recipe.jamanchu.repository;

import com.recipe.jamanchu.entity.IngredientEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<IngredientEntity, Long> {

  Optional<IngredientEntity> findByIngredientName(String ingredientName);
}
