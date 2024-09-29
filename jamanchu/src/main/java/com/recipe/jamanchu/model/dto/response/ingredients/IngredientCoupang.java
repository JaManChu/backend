package com.recipe.jamanchu.model.dto.response.ingredients;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class IngredientCoupang extends Ingredient {

  private final String ingredientCoupangLink;

  public IngredientCoupang(
      @NotEmpty @Size(min = 1) String ingredientName,
      @NotEmpty @Size(min = 1) String ingredientQuantity,
      @NotEmpty String ingredientCoupangLink) {

    super(ingredientName, ingredientQuantity);
    this.ingredientCoupangLink = ingredientCoupangLink;
  }
}
