package com.recipe.jamanchu.domain.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LevelType {
  LOW("초보"),
  MEDIUM("중급"),
  HIGH("고급");

  private final String level;

  public static LevelType fromString(String level) {
    if (level == null) {
      return null;
    }

    return switch (level) {
      case "중급" -> MEDIUM;
      case "고급" -> HIGH;
      default -> LOW;
    };
  }
}
