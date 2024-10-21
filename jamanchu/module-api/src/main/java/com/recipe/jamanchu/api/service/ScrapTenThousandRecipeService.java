package com.recipe.jamanchu.api.service;

import com.recipe.jamanchu.domain.model.dto.response.crawling.ScrapResult;
import java.util.List;

public interface ScrapTenThousandRecipeService {
  void saveCrawlRecipe(List<ScrapResult> scrapResult);
}
