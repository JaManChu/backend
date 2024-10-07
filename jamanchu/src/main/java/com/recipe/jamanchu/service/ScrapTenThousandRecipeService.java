package com.recipe.jamanchu.service;

import com.recipe.jamanchu.model.dto.response.crawling.ScrapResult;
import java.util.List;

public interface ScrapTenThousandRecipeService {
  void saveCrawlRecipe(List<ScrapResult> scrapResult);
}
