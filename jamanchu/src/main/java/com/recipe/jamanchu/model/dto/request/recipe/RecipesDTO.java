package com.recipe.jamanchu.model.dto.request.recipe;

import com.recipe.jamanchu.model.dto.response.ingredients.Ingredients;
import com.recipe.jamanchu.model.dto.response.recipes.RecipesManuals;
import com.recipe.jamanchu.model.type.CookingTimeType;
import com.recipe.jamanchu.model.type.LevelType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class RecipesDTO {

  @NotEmpty(message = "레시피 이름을 입력해주세요.")
  private final String recipeName;

  @NotNull(message = "레시피 레벨을 설정해주세요.")
  private final LevelType level;

  @NotNull(message = "레시피 소요시간을 설정해주세요.")
  private final CookingTimeType cookingTime;

  private final MultipartFile recipeImage;

  @NotNull(message = "레시피 재료를 입력해주세요.")
  private final Ingredients ingredients;

  @NotNull(message = "레시피 순서를 입력해주세요.")
  private final RecipesManuals orders;

}
