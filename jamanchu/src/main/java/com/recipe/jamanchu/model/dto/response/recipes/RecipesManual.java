package com.recipe.jamanchu.model.dto.response.recipes;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

/**
 * 단일 레시피 조리 순서
 */
@Getter
@AllArgsConstructor
public class RecipesManual {

  @Min(1)
  private final Long recipeOrder;

  @NotEmpty
  private final String recipeOrderContent;

  @NotEmpty
  private final MultipartFile recipeOrderImage;

}
