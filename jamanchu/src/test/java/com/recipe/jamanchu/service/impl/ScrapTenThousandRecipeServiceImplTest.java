package com.recipe.jamanchu.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.recipe.jamanchu.model.dto.response.crawling.ScrapResult;
import com.recipe.jamanchu.model.type.CookingTimeType;
import com.recipe.jamanchu.model.type.LevelType;
import com.recipe.jamanchu.repository.TenThousandRecipeRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScrapTenThousandRecipeServiceImplTest {

  @Mock
  private TenThousandRecipeRepository tenThousandRecipeRepository;

  @InjectMocks
  private ScrapTenThousandRecipeServiceImpl scrapTenThousandRecipeService;

  @Test
  @DisplayName("스크래핑 데이터 저장 성공")
  void saveCrawlRecipe_Success() {

    // given
    ScrapResult scrapResult1 = new ScrapResult(
        "Recipe 1", 1L, LevelType.LOW, CookingTimeType.FIFTEEN_MINUTES, "recipe1.jpg",
        4.5, 14, "Eggs, Butter", "Step 1, Step 2",
        "step1.jpg, step2.jpg");

    ScrapResult scrapResult2 = new ScrapResult(
        "Recipe 2", 2L, LevelType.MEDIUM, CookingTimeType.THIRTY_MINUTES, "recipe2.jpg",
        4.7, 5,"Chicken, Garlic", "Step 1, Step 2",
        "step1.jpg, step2.jpg");

    List<ScrapResult> scrapResults = Arrays.asList(scrapResult1, scrapResult2);

    // when
    scrapTenThousandRecipeService.saveCrawlRecipe(scrapResults);

    // Then
    assertEquals(scrapResult1.getTitle(), scrapResults.get(0).getTitle());
    assertEquals(scrapResult1.getRecipeId(), scrapResults.get(0).getRecipeId());
    assertEquals(scrapResult1.getLevelType(), scrapResults.get(0).getLevelType());
    assertEquals(scrapResult1.getCookTime(), scrapResults.get(0).getCookTime());
    assertEquals(scrapResult1.getRating(), scrapResults.get(0).getRating());
    assertEquals(scrapResult1.getReviewCount(), scrapResults.get(0).getReviewCount());
    assertEquals(scrapResult1.getIngredients(), scrapResults.get(0).getIngredients());
    assertEquals(scrapResult1.getManualContents(), scrapResults.get(0).getManualContents());
    assertEquals(scrapResult1.getManualPictures(), scrapResults.get(0).getManualPictures());
    assertEquals(scrapResult2.getTitle(), scrapResults.get(1).getTitle());
    assertEquals(scrapResult2.getRecipeId(), scrapResults.get(1).getRecipeId());
    assertEquals(scrapResult2.getLevelType(), scrapResults.get(1).getLevelType());
    assertEquals(scrapResult2.getCookTime(), scrapResults.get(1).getCookTime());
    assertEquals(scrapResult2.getRating(), scrapResults.get(1).getRating());
    assertEquals(scrapResult2.getReviewCount(), scrapResults.get(1).getReviewCount());
    assertEquals(scrapResult2.getIngredients(), scrapResults.get(1).getIngredients());
    assertEquals(scrapResult2.getManualContents(), scrapResults.get(1).getManualContents());
    assertEquals(scrapResult2.getManualPictures(), scrapResults.get(1).getManualPictures());


    // verify
    verify(tenThousandRecipeRepository, times(1)).saveAll(anyList());
  }

}