package com.recipe.jamanchu.domain.model.dto.response.recipes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;

/**
 * 레시피 조리 순서 List
 */
@Getter
public class RecipesManuals {

  @Size(min = 1, message = "조리 순서는 최소 1개 이상이어야 합니다.")
  @JsonProperty("recipesManuals")
  private final List<RecipesManual> recipesManuals;

  @JsonCreator
  public RecipesManuals(List<RecipesManual> recipesManuals) {
    this.recipesManuals = recipesManuals;
  }

}
