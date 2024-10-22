package com.recipe.jamanchu.domain.repository;

import com.recipe.jamanchu.domain.entity.RecommendRecipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendRecipeRepository extends JpaRepository<RecommendRecipeEntity, Long> {

}
