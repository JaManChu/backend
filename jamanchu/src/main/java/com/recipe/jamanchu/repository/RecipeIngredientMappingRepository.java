package com.recipe.jamanchu.repository;

import com.recipe.jamanchu.entity.RecipeIngredientMappingEntity;
import com.recipe.jamanchu.util.RecipeIngredientMappingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeIngredientMappingRepository extends JpaRepository<RecipeIngredientMappingEntity, RecipeIngredientMappingId> {

}
