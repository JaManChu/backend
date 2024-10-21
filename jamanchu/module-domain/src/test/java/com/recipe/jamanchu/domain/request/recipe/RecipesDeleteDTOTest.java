package com.recipe.jamanchu.domain.request.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecipesDeleteDTOTest {

  private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
  private final Validator validator = validatorFactory.getValidator();

  @DisplayName("레시피 ID는 반드시 1이상이여야 한다.")
  @Test
  void RecipeIdMustBiggerThan1() {
    //given
    RecipesDeleteDTO recipesDeleteDTO = new RecipesDeleteDTO(0L);
    //when
    Set<ConstraintViolation<RecipesDeleteDTO>> violations = validator.validate(recipesDeleteDTO);
    //then
    assertFalse(violations.isEmpty());
    assertEquals("잘못된 레시피 번호입니다.", violations.iterator().next().getMessage());
  }


}