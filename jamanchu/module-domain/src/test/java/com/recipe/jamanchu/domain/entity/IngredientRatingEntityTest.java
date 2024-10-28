package com.recipe.jamanchu.domain.entity;

import static com.recipe.jamanchu.domain.model.type.UserRole.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.domain.repository.IngredientRatingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IngredientRatingEntityTest {

  @Mock
  private IngredientRatingRepository ingredientRatingRepository;

  @Test
  @DisplayName("Ingredient Rating Entity Builder Test")
  void builder() {

    // given
    UserEntity ratingUser = UserEntity.builder()
        .usrId(1L)
        .usrNickname("test")
        .usrEmail("test@gmail.com")
        .usrPassword("test")
        .usrRole(USER)
        .build();

    IngredientEntity ingredient = IngredientEntity.builder()
        .ingId(1L)
        .ingName("ingredient")
        .build();

    IngredientRatingEntity ingredientRatingEntity = IngredientRatingEntity.builder()
        .user(ratingUser)
        .ingredient(ingredient)
        .irRating(5.0)
        .irPoint(5.0)
        .build();

    // when
    when(ingredientRatingRepository.save(ingredientRatingEntity)).thenReturn(ingredientRatingEntity);

    // act
    IngredientRatingEntity result = ingredientRatingRepository.save(ingredientRatingEntity);

    // then
    assertEquals(ingredientRatingEntity.getIngredient().getIngId(), result.getIngredient().getIngId());
    assertEquals(ingredientRatingEntity.getUser().getUsrId(), result.getUser().getUsrId());
    assertEquals(ingredientRatingEntity.getIrRating(), result.getIrRating());
    assertEquals(ingredientRatingEntity.getIrPoint(), result.getIrPoint());
  }
}