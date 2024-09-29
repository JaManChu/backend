package com.recipe.jamanchu.validator;

import com.recipe.jamanchu.annotation.AtLeastOneNotEmpty;
import com.recipe.jamanchu.model.dto.request.recipe.RecipesSearchDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AtLeastOneNotEmptyValidator implements ConstraintValidator<AtLeastOneNotEmpty, RecipesSearchDTO> {

  @Override
  public boolean isValid(RecipesSearchDTO dto, ConstraintValidatorContext context) {
    // ingredients가 null이 아니고 비어있지 않은지 확인
    boolean hasIngredients = dto.getIngredients() != null && !dto.getIngredients().isEmpty();

    // level이 null이 아닌지 확인
    boolean hasLevel = dto.getLevel() != null;

    // cookingTime이 null이 아닌지 확인
    boolean hasCookingTime = dto.getCookingTime() != null;

    // 하나라도 값이 있는지 확인
    return hasIngredients || hasLevel || hasCookingTime;
  }
}
