package com.recipe.jamanchu.domain.entity;

import static com.recipe.jamanchu.domain.model.type.CookingTimeType.*;
import static com.recipe.jamanchu.domain.model.type.LevelType.LOW;
import static com.recipe.jamanchu.domain.model.type.RecipeProvider.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.domain.repository.RecipeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecipeEntityTest {

  @Mock
  private RecipeRepository recipeRepository;

  @Test
  @DisplayName("Recipe Entity Builder Test")
  void builder() {

    // given
    UserEntity user = UserEntity.builder()
        .usrId(1L)
        .build();

    RecipeEntity recipeEntity = RecipeEntity.builder()
        .rcpId(1L)
        .user(user)
        .rcpName("Recipe1")
        .rcpLevel(LOW)
        .rcpTime(TEN_MINUTES)
        .rcpThumbnail("thumbnail1")
        .rcpProvider(USER)
        .build();

    // when
    when(recipeRepository.save(recipeEntity)).thenReturn(recipeEntity);
    // act
    RecipeEntity savedRecipe = recipeRepository.save(recipeEntity);
    // then
    assertEquals(recipeEntity.getUser().getUsrId(), savedRecipe.getUser().getUsrId());
    assertEquals(recipeEntity.getRcpName(), savedRecipe.getRcpName());
    assertEquals(recipeEntity.getRcpLevel(), savedRecipe.getRcpLevel());
    assertEquals(recipeEntity.getRcpTime(), savedRecipe.getRcpTime());
    assertEquals(recipeEntity.getRcpThumbnail(), savedRecipe.getRcpThumbnail());
    assertEquals(recipeEntity.getRcpProvider(), savedRecipe.getRcpProvider());
  }
}