package com.recipe.jamanchu.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.domain.repository.ManualRepository;
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
        .rcpId(1L)
        .build();

    ManualEntity manual = ManualEntity.builder()
        .mnId(1L)
        .recipe(recipe)
        .mnContent("content")
        .mnPicture("picture")
        .build();

    // when
    when(manualRepository.save(manual)).thenReturn(manual);
    // act
    ManualEntity savedManual = manualRepository.save(manual);
    // then
    assertEquals(manual.getRecipe().getRcpId(), savedManual.getRecipe().getRcpId());
    assertEquals(manual.getMnContent(), savedManual.getMnContent());
    assertEquals(manual.getMnPicture(), savedManual.getMnPicture());
  }
}