package com.recipe.jamanchu.domain.model.dto.response.crawling;

import com.recipe.jamanchu.domain.model.type.CookingTimeType;
import com.recipe.jamanchu.domain.model.type.LevelType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScrapResult {

  private String title;
  private Long recipeId;
  private LevelType levelType;
  private CookingTimeType cookTime;
  private String thumbnail;
  private Double rating;
  private Integer reviewCount;
  private String ingredients;
  private String manualContents;
  private String manualPictures;
}
