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
        .userId(1L)
        .nickname("test")
        .email("test@gmail.com")
        .password("test")
        .role(USER)
        .build();

    IngredientEntity ingredient = IngredientEntity.builder()
        .ingredientId(1L)
        .ingredientName("ingredient")
        .build();

    IngredientRatingEntity ingredientRatingEntity = IngredientRatingEntity.builder()
        .user(ratingUser)
        .ingredient(ingredient)
        .rating(5.0)
        .point(5.0)
        .build();

    // when
    when(ingredientRatingRepository.save(ingredientRatingEntity)).thenReturn(ingredientRatingEntity);

    // act
    IngredientRatingEntity result = ingredientRatingRepository.save(ingredientRatingEntity);

    // then
    assertEquals(ingredientRatingEntity.getIngredient().getIngredientId(), result.getIngredient().getIngredientId());
    assertEquals(ingredientRatingEntity.getUser().getUserId(), result.getUser().getUserId());
    assertEquals(ingredientRatingEntity.getRating(), result.getRating());
    assertEquals(ingredientRatingEntity.getPoint(), result.getPoint());
  }
}