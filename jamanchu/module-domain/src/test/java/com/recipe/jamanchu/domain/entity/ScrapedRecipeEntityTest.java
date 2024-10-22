package com.recipe.jamanchu.domain.entity;

import static com.recipe.jamanchu.domain.model.type.UserRole.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.domain.repository.ScrapedRecipeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScrapedRecipeEntityTest {

  @Mock
  private ScrapedRecipeRepository scrapedRecipeRepository;

  @Test
  @DisplayName("Scraped Recipe Entity Builder Test")
  void builder() {

    // given
    RecipeEntity recipe = RecipeEntity.builder()
        .id(1L)
        .build();

    UserEntity user = UserEntity.builder()
        .userId(1L)
        .nickname("user")
        .email("user@gmail.com")
        .password("1234")
        .role(USER)
        .build();

    ScrapedRecipeEntity scrapedRecipeEntity = ScrapedRecipeEntity.builder()
        .recipe(recipe)
        .user(user)
        .build();

    // when
    when(scrapedRecipeRepository.save(scrapedRecipeEntity)).thenReturn(scrapedRecipeEntity);

    // act
    ScrapedRecipeEntity savedScrapedRecipe = scrapedRecipeRepository.save(scrapedRecipeEntity);

    // then
    assertEquals(scrapedRecipeEntity.getRecipe().getId(), savedScrapedRecipe.getRecipe().getId());

  }
}