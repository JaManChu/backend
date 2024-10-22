package com.recipe.jamanchu.domain.request.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.recipe.jamanchu.domain.model.dto.request.recipe.RecipesDTO;
import com.recipe.jamanchu.domain.model.dto.response.ingredients.Ingredient;
import com.recipe.jamanchu.domain.model.dto.response.recipes.RecipesManual;
import com.recipe.jamanchu.domain.model.type.CookingTimeType;
import com.recipe.jamanchu.domain.model.type.LevelType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecipesDTOTest {

  private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
  private final Validator validator = validatorFactory.getValidator();

  @DisplayName("레시피 이름은 반드시 존재해야한다.")
  @Test
  void MustHaveRecipeName() {
    //given
    RecipesDTO recipesDTO = new RecipesDTO(
        null,
        LevelType.LOW,
        CookingTimeType.TEN_MINUTES,
        null,
            List.of(
                new Ingredient("재료", "재료 양")
            )
        ,

            List.of(
                new RecipesManual("레시피 순서", "레시피 이미지")
            )
        );
    //when
    Set<ConstraintViolation<RecipesDTO>> violations = validator.validate(recipesDTO);
    //then
    assertFalse(violations.isEmpty());
    assertEquals("레시피 이름을 입력해주세요.", violations.iterator().next().getMessage());
  }

  @DisplayName("레시피 사진을 제외하고 모든 내용들이 들어있어야한다.")
  @Test
  void MustHaveAllContentsExceptRecipeImage() {
    //given
    RecipesDTO recipesDTO = new RecipesDTO(
        null,
        null,
        null,
        null,
        null,
        null
    );
    //when
    Set<ConstraintViolation<RecipesDTO>> violations = validator.validate(recipesDTO);
    //then
    assertFalse(violations.isEmpty());
    String[] messages = new String[]{
        "레시피 이름을 입력해주세요.",
        "레시피 레벨을 설정해주세요.",
        "레시피 소요시간을 설정해주세요.",
        "레시피 재료를 입력해주세요.",
        "레시피 순서를 입력해주세요."
    };
    List<String> violationMessages = violations.stream()
        .map(ConstraintViolation::getMessage)
        .toList();

    for (String message : messages) {
      assertTrue(violationMessages.contains(message),
          '"' + message + '"' + " 메시지가 포함되어 있어야 합니다.");
    }
  }
}