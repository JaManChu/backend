package com.recipe.jamanchu.api.service.impl;

import com.recipe.jamanchu.domain.entity.TenThousandRecipeEntity;
import com.recipe.jamanchu.domain.model.dto.response.crawling.ScrapResult;
import com.recipe.jamanchu.domain.repository.TenThousandRecipeRepository;
import com.recipe.jamanchu.api.service.ScrapTenThousandRecipeService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScrapTenThousandRecipeServiceImpl implements ScrapTenThousandRecipeService {

  private final TenThousandRecipeRepository crawledRecipeRepository;

  @Override
  public void saveCrawlRecipe(List<ScrapResult> scrapResults) {
    crawledRecipeRepository.saveAll(scrapResults.stream()
        .map(scrapResult -> TenThousandRecipeEntity.builder()
            .trName(scrapResult.getTitle())
            .trOriginId(scrapResult.getRecipeId())
            .trLevel(scrapResult.getLevelType())
            .trCookTime(scrapResult.getCookTime())
            .trIngredients(scrapResult.getIngredients())
            .trThumbnail(scrapResult.getThumbnail())
            .trRating(scrapResult.getRating())
            .trReviewCount(scrapResult.getReviewCount())
            .trMnContents(scrapResult.getManualContents())
            .trMnPictures(scrapResult.getManualPictures())
            .build())
        .collect(Collectors.toList()));
  }
}
