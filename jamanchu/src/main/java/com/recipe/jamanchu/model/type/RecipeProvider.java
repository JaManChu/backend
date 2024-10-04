package com.recipe.jamanchu.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RecipeProvider {
  SCRAP("만개의 레시피"),
  USER("사용자");

  private final String provider;
}
