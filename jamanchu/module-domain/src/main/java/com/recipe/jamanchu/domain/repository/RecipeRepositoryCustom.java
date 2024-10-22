package com.recipe.jamanchu.repository;

import com.recipe.jamanchu.entity.RecipeEntity;
import com.recipe.jamanchu.model.dto.request.recipe.RecipesSearchDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecipeRepositoryCustom {
  Page<RecipeEntity> searchAndRecipesQueryDSL(RecipesSearchDTO recipesSearchDTO,
      List<Long> scrapedRecipeIds, Pageable pageable);

  Page<RecipeEntity> searchOrRecipesQueryDSL(RecipesSearchDTO recipesSearchDTO,
      List<Long> scrapedRecipeIds, Pageable pageable);
}
