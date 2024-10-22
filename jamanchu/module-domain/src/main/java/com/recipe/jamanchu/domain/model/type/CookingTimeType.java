package com.recipe.jamanchu.domain.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CookingTimeType {
  FIVE_MINUTES("5분 이내"),
  TEN_MINUTES("10분 이내"),
  FIFTEEN_MINUTES("15분 이내"),
  TWENTY_MINUTES("20분 이내"),
  THIRTY_MINUTES("30분 이내"),
  ONE_HOUR("60분 이내"),
  TWO_HOURS("60분 이상")
  ;

  private final String time;

  public static CookingTimeType fromString(String cookTime) {
    if (cookTime == null) {
      return null; // null 값 처리
    }
    return switch (cookTime) {
      case "5분 이내" -> FIVE_MINUTES;
      case "10분 이내" -> TEN_MINUTES;
      case "15분 이내" -> FIFTEEN_MINUTES;
      case "20분 이내" -> TWENTY_MINUTES;
      case "30분 이내" -> THIRTY_MINUTES;
      case "60분 이내" -> ONE_HOUR;
      default -> TWO_HOURS;
    };
  }
}
