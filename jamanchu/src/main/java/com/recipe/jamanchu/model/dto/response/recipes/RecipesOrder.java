package com.recipe.jamanchu.model.dto.response.recipes;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class RecipesOrder {

  @Min(1)
  private final Long recipeOrder;

  @NotEmpty
  private final String recipeOrderContent;

  @NotEmpty
  private final MultipartFile recipeOrderImage;

}
