package com.recipe.jamanchu.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.domain.model.type.UserRole;
import com.recipe.jamanchu.domain.repository.RecipeRatingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecipeRatingEntityTest {

  @Mock
  private RecipeRatingRepository recipeRatingRepository;

  @Test
  @DisplayName("Recipe Rating Entity Builder Test")
  void builder() {

    // given
    RecipeEntity recipe = RecipeEntity.builder()
        .rcpId(1L)
        .build();

    UserEntity user = UserEntity.builder()
        .usrId(1L)
        .usrNickname("user")
        .usrEmail("user@gmail.com")
        .usrPassword("1234")
        .usrRole(UserRole.USER)
        .build();

    RecipeRatingEntity recipeRatingEntity = RecipeRatingEntity.builder()
        .recipe(recipe)
        .user(user)
        .rrRating(5.0)
        .build();

    // when
    when(recipeRatingRepository.save(recipeRatingEntity)).thenReturn(recipeRatingEntity);

    // act
    RecipeRatingEntity savedRating = recipeRatingRepository.save(recipeRatingEntity);

    // then
    assertEquals(recipeRatingEntity.getRecipe().getRcpId(), savedRating.getRecipe().getRcpId());
  }
}