package com.recipe.jamanchu.model.dto.request.recipe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.recipe.jamanchu.model.dto.response.ingredients.Ingredient;
import com.recipe.jamanchu.model.dto.response.recipes.RecipesManual;
import com.recipe.jamanchu.model.type.CookingTimeType;
import com.recipe.jamanchu.model.type.LevelType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class RecipesDTO {

  @NotEmpty(message = "레시피 이름을 입력해주세요.")
  @JsonProperty("recipeName")
  private final String recipeName;

  @NotNull(message = "레시피 레벨을 설정해주세요.")
  @JsonProperty("recipeLevel")
  private final LevelType recipeLevel;

  @NotNull(message = "레시피 소요시간을 설정해주세요.")
  @JsonProperty("recipeCookingTime")
  private final CookingTimeType recipeCookingTime;

  @JsonProperty("recipeThumbnail")
  private final MultipartFile recipeThumbnail;

  @NotNull(message = "레시피 재료를 입력해주세요.")
  @JsonProperty("recipeIngredients")
  private final List<Ingredient> recipeIngredients;

  @NotNull(message = "레시피 순서를 입력해주세요.")
  @JsonProperty("recipeOrderContents")
  private final List<RecipesManual> recipeOrderContents;

  @JsonCreator
  public RecipesDTO(String recipeName, LevelType recipeLevel, CookingTimeType recipeCookingTime, MultipartFile recipeThumbnail, List<Ingredient> recipeIngredients, List<RecipesManual> recipeOrderContents) {
    this.recipeName = recipeName;
    this.recipeLevel = recipeLevel;
    this.recipeCookingTime = recipeCookingTime;
    this.recipeThumbnail = recipeThumbnail;
    this.recipeIngredients = recipeIngredients;
    this.recipeOrderContents = recipeOrderContents;
  }
}
