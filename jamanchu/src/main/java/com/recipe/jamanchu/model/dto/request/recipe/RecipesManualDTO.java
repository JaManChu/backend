package com.recipe.jamanchu.model.dto.request.recipe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class RecipesManualDTO {

  @NotEmpty(message = "조리 과정을 설명해주세요.")
  @JsonProperty("recipeOrderContent")
  private final String recipeOrderContent;

  @JsonProperty("recipeOrderImage")
  private final MultipartFile recipeOrderImage;

  @JsonCreator
  public RecipesManualDTO(String recipeOrderContent, MultipartFile recipeOrderImage) {
    this.recipeOrderContent = recipeOrderContent;
    this.recipeOrderImage = recipeOrderImage;
  }
}
