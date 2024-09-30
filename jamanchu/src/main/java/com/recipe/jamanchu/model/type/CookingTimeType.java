package com.recipe.jamanchu.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CookingTimeType {
  TEN_MINUTES("10분 이내"),
  FIFTEEN_MINUTES("15분 이내"),
  THIRTY_MINUTES("30분 이내"),
  ONE_HOURS("60분 이내"),
  TWO_HOURS("60분 이상")
  ;

  private final String time;
}
