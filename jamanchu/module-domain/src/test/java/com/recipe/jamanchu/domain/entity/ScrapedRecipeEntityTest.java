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
        .rcpId(1L)
        .build();

    UserEntity user = UserEntity.builder()
        .usrId(1L)
        .usrNickname("user")
        .usrEmail("user@gmail.com")
        .usrPassword("1234")
        .usrRole(USER)
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
    assertEquals(scrapedRecipeEntity.getRecipe().getRcpId(), savedScrapedRecipe.getRecipe().getRcpId());

  }
}