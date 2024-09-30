package com.recipe.jamanchu.model.dto.response.ingredients;

import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Ingredients {

  @Size(min = 1, message = "재료는 최소 1개 이상이어야 합니다.")
  private final List<Ingredient> ingredients;

}
