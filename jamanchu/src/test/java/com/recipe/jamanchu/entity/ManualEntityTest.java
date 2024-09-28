package com.recipe.jamanchu.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.repository.ManualRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ManualEntityTest
 * 추후 Merge 후 수정 예정
 */
@ExtendWith(MockitoExtension.class)
class ManualEntityTest {

  @Mock
  private ManualRepository manualRepository;

  @Test
  @DisplayName("Manual Entity Builder Test")
  void builder() {

    // given
    RecipeEntity recipe = RecipeEntity.builder()
        .id(1L)
        .build();

    ManualEntity manual = ManualEntity.builder()
        .manualId(1L)
        .recipe(recipe)
        .manualContent("content")
        .manualPicture("picture")
        .build();

    // when
    when(manualRepository.save(manual)).thenReturn(manual);
    // act
    ManualEntity savedManual = manualRepository.save(manual);
    // then
    assertEquals(manual.getRecipe().getId(), savedManual.getRecipe().getId());
    assertEquals(manual.getManualContent(), savedManual.getManualContent());
    assertEquals(manual.getManualPicture(), savedManual.getManualPicture());
  }
}