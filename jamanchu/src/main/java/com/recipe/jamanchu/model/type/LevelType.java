package com.recipe.jamanchu.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LevelType {
  LOW("초보"),
  MEDIUM("중급"),
  HIGH("고급");

  private final String level;
}
