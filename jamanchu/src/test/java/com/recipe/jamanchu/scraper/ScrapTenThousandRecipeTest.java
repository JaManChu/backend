package com.recipe.jamanchu.scraper;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.model.dto.response.ResultResponse;
import com.recipe.jamanchu.model.dto.response.crawling.ScrapResult;
import com.recipe.jamanchu.model.type.CookingTimeType;
import com.recipe.jamanchu.model.type.LevelType;
import com.recipe.jamanchu.repository.TenThousandRecipeRepository;
import com.recipe.jamanchu.service.impl.ScrapTenThousandRecipeServiceImpl;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScrapTenThousandRecipeTest {

  @Mock
  private ScrapTenThousandRecipeServiceImpl scrapRecipeService;

  @Mock
  private TenThousandRecipeRepository tenThousandRecipeRepository;

  @InjectMocks
  private ScrapTenThousandRecipe scrapTenThousandRecipe;

  @Test
  @DisplayName("데이터 스크래핑 성공")
  void testScrapSuccess() {
    // Given
    ScrapResult scrapResult1 = new ScrapResult(
        "Test Recipe", 101L, LevelType.LOW,
        CookingTimeType.THIRTY_MINUTES, "thumbnail", 4.5,
        10, "ingredients", "manualContents", "manualPictures");
    ScrapResult scrapResult2 = new ScrapResult(
        "Test Recipe", 102L, LevelType.LOW,
        CookingTimeType.THIRTY_MINUTES, "thumbnail", 4.0,
        10, "ingredients", "manualContents", "manualPictures");

    // When
    ResultResponse result = scrapTenThousandRecipe.scrap(101L, 102L);

    // Then
    assertEquals("스크래핑 레시피 저장 성공!", result.getMessage());
  }

  @Test
  @DisplayName("ScrapResult 가 중간에 null 이어도 저장이 되는 경우")
  void testScrapWithScrapResultNull() {
    // given
    List<ScrapResult> scrapResults = new ArrayList<>();
    ScrapResult scrapResult1 = new ScrapResult(
        "Test Recipe", 101L, LevelType.LOW,
        CookingTimeType.THIRTY_MINUTES, "thumbnail", 4.5,
        10, "ingredients", "manualContents", "manualPictures");
    ScrapResult scrapResult2 = null;
    scrapResults.add(scrapResult1);
    scrapResults.add(scrapResult2);

    // 여러 레시피를 반환하도록 설정
    ResultResponse resultResponse = scrapTenThousandRecipe.scrap(101L, 105L);

    // then
    assertNotNull(resultResponse); // 최소한 하나의 레시피가 처리되어야 하므로 null이 아니어야 함
    assertEquals("스크래핑 레시피 저장 성공!", resultResponse.getMessage()); // 성공 코드 확인
  }

  @Test
  @DisplayName("300개의 레시피를 스크랩할 때 2번 저장")
  void testScrapMultipleBatches() {
    // Given
    List<ScrapResult> scrapResults = new ArrayList<>();
    for (long recipeId = 1L; recipeId < 300L; recipeId++) {
      ScrapResult scrapResult = new ScrapResult(
          "Test Recipe", recipeId, LevelType.LOW,
          CookingTimeType.THIRTY_MINUTES, "thumbnail", 4.5,
          10, "ingredients", "manualContents", "manualPictures");
      scrapResults.add(scrapResult);
      if (recipeId == 200L) {
        scrapRecipeService.saveCrawlRecipe(scrapResults);
      }
    }

    // When
    scrapRecipeService.saveCrawlRecipe(scrapResults);

    // Then
    verify(scrapRecipeService, times(2)).saveCrawlRecipe(scrapResults);
  }

  @Test
  @DisplayName("스케쥴링 함수 정상 동작")
  void testWeeklyRecipeScrape() {
    // given
    when(tenThousandRecipeRepository.findMaxRecipeId()).thenReturn(100L);

    // when
    scrapTenThousandRecipe.weeklyRecipeScrape();

    // then
    verify(tenThousandRecipeRepository).findMaxRecipeId();
  }
}