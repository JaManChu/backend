package com.recipe.jamanchu.model.dto.request.recipe;

import static org.junit.jupiter.api.Assertions.*;

import com.recipe.jamanchu.model.dto.response.ingredients.Ingredient;
import com.recipe.jamanchu.model.dto.response.ingredients.Ingredients;
import com.recipe.jamanchu.model.dto.response.recipes.RecipesManual;
import com.recipe.jamanchu.model.dto.response.recipes.RecipesManuals;
import com.recipe.jamanchu.model.type.CookingTimeType;
import com.recipe.jamanchu.model.type.LevelType;
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
        new Ingredients(List.of(
            new Ingredient("재료 이름", "재료 양")
        )),
        new RecipesManuals(List.of(
            new RecipesManual(1L, "레시피 순서", null)
        )),
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
        new Ingredients(List.of(
            new Ingredient("재료 이름", "재료 양")
        )),
        new RecipesManuals(List.of(
            new RecipesManual(1L, "레시피 순서", null)
        )),
        null
    );

    //when
    Set<ConstraintViolation<RecipesUpdateDTO>> violations = validator.validate(recipesDTO);
    //then
    assertFalse(violations.isEmpty());
    assertEquals("레시피 아이디가 없습니다.", violations.iterator().next().getMessage());
  }
}