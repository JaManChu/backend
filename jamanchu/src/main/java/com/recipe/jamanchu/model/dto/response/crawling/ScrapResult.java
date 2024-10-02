package com.recipe.jamanchu.model.dto.response.crawling;

import com.recipe.jamanchu.model.type.CookingTimeType;
import com.recipe.jamanchu.model.type.LevelType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScrapResult {

  private String title;
  private String authorName;
  private String description;
  private LevelType levelType;
  private CookingTimeType cookTime;
  private String thumbnail;
  private Double rating;
  private String ingredients;
  private String manualContents;
  private String manualPictures;
}
