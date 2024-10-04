package com.recipe.jamanchu.entity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.model.type.CookingTimeType;
import com.recipe.jamanchu.model.type.LevelType;
import com.recipe.jamanchu.model.type.RecipeProvider;
import com.recipe.jamanchu.repository.RecipeRepository;
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
        .userId(1L)
        .build();

    RecipeEntity recipeEntity = RecipeEntity.builder()
        .id(1L)
        .user(user)
        .name("Recipe1")
        .level(LevelType.LOW)
        .time(CookingTimeType.TEN_MINUTES)
        .thumbnail("thumbnail1")
        .provider(RecipeProvider.USER)
        .build();

    // when
    when(recipeRepository.save(recipeEntity)).thenReturn(recipeEntity);
    // act
    RecipeEntity savedRecipe = recipeRepository.save(recipeEntity);
    // then
    assertEquals(recipeEntity.getUser().getUserId(), savedRecipe.getUser().getUserId());
    assertEquals(recipeEntity.getName(), savedRecipe.getName());
    assertEquals(recipeEntity.getLevel(), savedRecipe.getLevel());
    assertEquals(recipeEntity.getTime(), savedRecipe.getTime());
    assertEquals(recipeEntity.getThumbnail(), savedRecipe.getThumbnail());
    assertEquals(recipeEntity.getProvider(), savedRecipe.getProvider());
  }
}