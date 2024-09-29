package com.recipe.jamanchu.model.dto.response.ingredients;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Ingredient {

  @NotEmpty
  @Size(min = 1)
  private final String ingredientName;

  @NotEmpty
  @Size(min = 1)
  private final String ingredientQuantity;

}
