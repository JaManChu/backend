package com.recipe.jamanchu.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.recipe.jamanchu.model.dto.response.ResultResponse;
import com.recipe.jamanchu.model.dto.response.crawling.ScrapResult;
import com.recipe.jamanchu.model.type.CookingTimeType;
import com.recipe.jamanchu.model.type.LevelType;
import com.recipe.jamanchu.repository.TenThousandRecipeRepository;
import java.util.Arrays;
import java.util.List;
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
  void saveCrawlRecipe_Success() {

    // given
    ScrapResult scrapResult1 = new ScrapResult(
        "Recipe 1", "Author 1", "Delicious recipe 1",
        LevelType.LOW, CookingTimeType.FIFTEEN_MINUTES, "recipe1.jpg",
        4.5, "Eggs, Butter", "Step 1, Step 2",
        "step1.jpg, step2.jpg");

    ScrapResult scrapResult2 = new ScrapResult(
        "Recipe 2", "Author 2", "Delicious recipe 2",
        LevelType.MEDIUM, CookingTimeType.THIRTY_MINUTES, "recipe2.jpg",
        4.7, "Chicken, Garlic", "Step 1, Step 2",
        "step1.jpg, step2.jpg");

    List<ScrapResult> scrapResults = Arrays.asList(scrapResult1, scrapResult2);

    // when
    ResultResponse resultResponse = scrapTenThousandRecipeService.saveCrawlRecipe(scrapResults);

    // Then
    assertEquals("스크래핑 레시피 저장 성공!", resultResponse.getMessage());

    // verify
    verify(tenThousandRecipeRepository, times(1)).saveAll(anyList());
  }

}