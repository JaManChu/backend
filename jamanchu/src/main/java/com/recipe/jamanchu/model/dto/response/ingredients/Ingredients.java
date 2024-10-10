package com.recipe.jamanchu.model.dto.response.ingredients;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;

@Getter
public class Ingredients {

  @Size(min = 1, message = "재료는 최소 1개 이상이어야 합니다.")
  @JsonProperty("ingredients")
  private final List<Ingredient> ingredients;

  @JsonCreator
  public Ingredients(List<Ingredient> ingredients) {
    this.ingredients = ingredients;
  }
}
