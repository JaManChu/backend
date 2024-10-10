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
  @JsonProperty("level")
  private final LevelType level;

  @NotNull(message = "레시피 소요시간을 설정해주세요.")
  @JsonProperty("cookingTime")
  private final CookingTimeType cookingTime;

  @JsonProperty("recipeImage")
  private final MultipartFile recipeImage;

  @NotNull(message = "레시피 재료를 입력해주세요.")
  @JsonProperty("ingredients")
  private final List<Ingredient> ingredients;

  @NotNull(message = "레시피 순서를 입력해주세요.")
  @JsonProperty("manuals")
  private final List<RecipesManual> manuals;

  @JsonCreator
  public RecipesDTO(String recipeName, LevelType level, CookingTimeType cookingTime, MultipartFile recipeImage, List<Ingredient> ingredients, List<RecipesManual> manuals) {
    this.recipeName = recipeName;
    this.level = level;
    this.cookingTime = cookingTime;
    this.recipeImage = recipeImage;
    this.ingredients = ingredients;
    this.manuals = manuals;
  }
}
