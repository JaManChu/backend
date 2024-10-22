package com.recipe.jamanchu.domain.repository;

import com.recipe.jamanchu.domain.entity.RecipeEntity;
import com.recipe.jamanchu.domain.model.dto.request.recipe.RecipesSearchDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecipeRepositoryCustom {
  Page<RecipeEntity> searchAndRecipesQueryDSL(RecipesSearchDTO recipesSearchDTO,
      List<Long> scrapedRecipeIds, Pageable pageable);

  Page<RecipeEntity> searchOrRecipesQueryDSL(RecipesSearchDTO recipesSearchDTO,
      List<Long> scrapedRecipeIds, Pageable pageable);
}
