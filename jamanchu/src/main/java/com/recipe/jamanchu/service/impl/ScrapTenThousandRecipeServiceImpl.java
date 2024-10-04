package com.recipe.jamanchu.service.impl;

import com.recipe.jamanchu.entity.TenThousandRecipeEntity;
import com.recipe.jamanchu.model.dto.response.ResultResponse;
import com.recipe.jamanchu.model.dto.response.crawling.ScrapResult;
import com.recipe.jamanchu.model.type.ResultCode;
import com.recipe.jamanchu.repository.TenThousandRecipeRepository;
import com.recipe.jamanchu.service.ScrapTenThousandRecipeService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScrapTenThousandRecipeServiceImpl implements ScrapTenThousandRecipeService {

  private final TenThousandRecipeRepository crawledRecipeRepository;

  @Override
  @Transactional
  public ResultResponse saveCrawlRecipe(List<ScrapResult> scrapResults) {
    crawledRecipeRepository.saveAll(scrapResults.stream()
        .map(scrapResult -> TenThousandRecipeEntity.builder()
            .name(scrapResult.getTitle())
            .levelType(scrapResult.getLevelType())
            .cookingTimeType(scrapResult.getCookTime())
            .ingredients(scrapResult.getIngredients())
            .thumbnail(scrapResult.getThumbnail())
            .rating(scrapResult.getRating())
            .crReviewCount(scrapResult.getReviewCount())
            .crManualContents(scrapResult.getManualContents())
            .crManualPictures(scrapResult.getManualPictures())
            .build())
        .collect(Collectors.toList()));

    return ResultResponse.of(ResultCode.SUCCESS_CR_RECIPE);
  }
}
