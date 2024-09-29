package com.recipe.jamanchu.model.dto.request.recipe;

import com.recipe.jamanchu.annotation.AtLeastOneNotEmpty;
import com.recipe.jamanchu.model.type.CookingTimeType;
import com.recipe.jamanchu.model.type.LevelType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@AtLeastOneNotEmpty(message = "하나 이상의 검색 조건을 입력해야 합니다.")
public class RecipesSearchDTO {

  private final List<String> ingredients;

  private final LevelType level;

  private final CookingTimeType cookingTime;

}
