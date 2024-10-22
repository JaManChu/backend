package com.recipe.jamanchu.domain.request.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.recipe.jamanchu.domain.model.dto.request.recipe.RecipesUpdateDTO;
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
class RecipesUpdateDTOTest {

  private final ValidatorFactory factor = Validation.buildDefaultValidatorFactory();
  private final Validator validator = factor.getValidator();

  @DisplayName("레시피 ID는 반드시 1이상이여야 한다.")
  @Test
  void RecipeIDBiggerThan1() {
    //given
    RecipesUpdateDTO recipesDTO = new RecipesUpdateDTO(
        "레시피 이름",
        LevelType.LOW,
        CookingTimeType.TEN_MINUTES,
        null,
        List.of(
            new Ingredient("재료", "재료 양")
        ),
        List.of(
            new RecipesManual("레시피 순서", "레시피 이미지")
        ),
        0L
    );

    //when
    Set<ConstraintViolation<RecipesUpdateDTO>> violations = validator.validate(recipesDTO);
    //then
    assertFalse(violations.isEmpty());
    assertEquals("레시피 아이디는 1 이상이어야 합니다.", violations.iterator().next().getMessage());
  }

  @DisplayName("레시피 ID는 반드시 존재해야 한다.")
  @Test
  void RecipeIDNotNull() {
    //given
    RecipesUpdateDTO recipesDTO = new RecipesUpdateDTO(
        "레시피 이름",
        LevelType.LOW,
        CookingTimeType.TEN_MINUTES,
        null,
        List.of(
            new Ingredient("재료", "재료 양")
        ),
        List.of(
            new RecipesManual("레시피 순서", "레시피 이미지")
        ),
        null
    );

    //when
    Set<ConstraintViolation<RecipesUpdateDTO>> violations = validator.validate(recipesDTO);
    //then
    assertFalse(violations.isEmpty());
    assertEquals("레시피 아이디가 없습니다.", violations.iterator().next().getMessage());
  }
}