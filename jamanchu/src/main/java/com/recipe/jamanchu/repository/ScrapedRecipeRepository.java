package com.recipe.jamanchu.repository;

import com.recipe.jamanchu.entity.ScrapedRecipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScrapedRecipeRepository extends JpaRepository<ScrapedRecipeEntity, Long> {

}
