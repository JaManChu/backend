package com.recipe.jamanchu.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RecipeProvider {
  SCRAP("스크랩"),
  USER("사용자");

  private final String provider;
}
