package com.recipe.jamanchu.model.dto.request.recipe;

import static org.junit.jupiter.api.Assertions.*;

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
class RecipesSearchDTOTest {

  private final ValidatorFactory factor = Validation.buildDefaultValidatorFactory();
  private final Validator validator = factor.getValidator();

  @DisplayName("검색 조건은 반드시 하나 이상 있어야한다.")
  @Test
  void AtLeastOneOption() {
    //given
    RecipesSearchDTO recipesDTO = new RecipesSearchDTO(
        null, null, null
    );
    //when
    Set<ConstraintViolation<RecipesSearchDTO>> violations = validator.validate(recipesDTO);
    //then
    assertFalse(violations.isEmpty());
    assertEquals("하나 이상의 검색 조건을 입력해야 합니다.", violations.iterator().next().getMessage());
  }

}