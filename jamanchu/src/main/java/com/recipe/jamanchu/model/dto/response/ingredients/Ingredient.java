package com.recipe.jamanchu.model.dto.response.ingredients;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Ingredient {

  @Size(min = 1, message = "재료 이름을 입력해주세요.")
  private final String ingredientName;

  @Size(min = 1, message = "재료 양을 입력해주세요.")
  private final String ingredientQuantity;

}
