package com.recipe.jamanchu.scraper;

import static com.recipe.jamanchu.model.type.ResultCode.*;

import com.recipe.jamanchu.model.dto.response.ResultResponse;
import com.recipe.jamanchu.model.dto.response.crawling.ScrapResult;
import com.recipe.jamanchu.model.type.CookingTimeType;
import com.recipe.jamanchu.model.type.LevelType;
import com.recipe.jamanchu.repository.TenThousandRecipeRepository;
import com.recipe.jamanchu.service.ScrapTenThousandRecipeService;
import com.recipe.jamanchu.util.LastRecipeIdUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ScrapTenThousandRecipe {

  private static final String url = "https://www.10000recipe.com/recipe/";
  private final ScrapTenThousandRecipeService scrapRecipeService;
  private final TenThousandRecipeRepository tenThousandRecipeRepository;
  private final LastRecipeIdUtil lastRecipeIdUtil;

  public ResultResponse scrap(Long startRecipeId, Long stopRecipeId) {
    List<ScrapResult> recipeBatch = new ArrayList<>();

    while (stopRecipeId >= startRecipeId) {
      String recipeUrl = url + startRecipeId;
      ScrapResult result = scrapeRecipeDetails(recipeUrl);
      if (result != null) {
        recipeBatch.add(result);
        System.out.println("success add result");
      }

      if (recipeBatch.size() >= 200) {
        scrapRecipeService.saveCrawlRecipe(recipeBatch);
        recipeBatch.clear();
      }
      startRecipeId++;
    }

    // 남은 레시피 저장 (200개 미만일 경우)
    if (!recipeBatch.isEmpty()) {
      scrapRecipeService.saveCrawlRecipe(recipeBatch);
    }

    return ResultResponse.of(SUCCESS_CR_RECIPE);
  }

  @Scheduled(cron = "0 0 0 * * SUN")
  public void weeklyRecipeScrape() {
    Long lastRecipeId = tenThousandRecipeRepository.findMaxRecipeId();
    lastRecipeIdUtil.setLastRecipeId(lastRecipeId);
    scrap(lastRecipeId + 1, lastRecipeId + 200);
  }

  private ScrapResult scrapeRecipeDetails(String url) {
    try {
      // 레시피 상세 페이지 파싱
      Document recipeDoc = Jsoup.connect(url).get();

      // title 추출
      String title = getText(recipeDoc, ".view2_summary h3");
      if (title == null) return null;

      // 조리 순서 및 이미지
      Elements stepsElements = recipeDoc.select(".view_step_cont");
      Elements stepImagesElements = recipeDoc.select(".view_step_cont img");

      if (stepsElements.isEmpty()) return null;

      StringBuilder manualContents = new StringBuilder();
      StringBuilder manualPictures = new StringBuilder();

      for (Element step : stepsElements) {
        manualContents.append(step.text()).append("$%^");
      }

      for (Element image : stepImagesElements) {
        manualPictures.append(image.attr("src")).append(",");
      }

      // ingredient 추출
      Element ingredientElement = recipeDoc.selectFirst("#divConfirmedMaterialArea");
      StringBuilder ingredients = new StringBuilder();
      if (ingredientElement != null) {
        Elements ingredientLists = ingredientElement.select("ul");
        for (Element ingredientList : ingredientLists) {
          // 각 재료 항목 추출
          Elements items = ingredientList.select("li");
          for (Element item : items) {
            String ingredientName = item.selectFirst(".ingre_list_name").text();
            String ingredientAmount = item.selectFirst(".ingre_list_ea").text();
            ingredients.append(ingredientName).append(" ").append(ingredientAmount).append(",");
          }
        }
      } else {
        return null;
      }

      // recipeId 추출
      String[] urlParts = url.split("/");
      Long recipeId = Long.parseLong(urlParts[urlParts.length - 1]);

      // level 추출
      String level = getText(recipeDoc, ".view2_summary_info3");
      LevelType levelType = LevelType.fromString(level);

      // cookTime 추출
      String cookTime = getText(recipeDoc, ".view2_summary_info2");
      CookingTimeType cookingTimeType = CookingTimeType.fromString(cookTime);

      // thumbnail 추출
      String thumbnail = getAttr(recipeDoc, ".centeredcrop img", "src");

      // averageRating 계산
      List<Integer> reviewsRating = scrapeReviewsRating(recipeDoc);
      int reviewCount = reviewsRating.size();
      double averageRating = reviewsRating.stream()
          .mapToInt(Integer::intValue)
          .average()
          .orElse(0.0);

      averageRating = Math.round(averageRating * 100.0) / 100.0;

      // ScrapResult 객체 반환
      return new ScrapResult(title, recipeId, levelType, cookingTimeType, thumbnail, averageRating, reviewCount, ingredients.toString(),
          manualContents.toString(), manualPictures.toString());

    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;  // 에러 발생 시 null 반환
  }

  private String getText(Document doc, String cssQuery) {
    Element element = doc.selectFirst(cssQuery);
    return (element != null) ? element.text() : null;
  }

  private String getAttr(Document doc, String cssQuery, String attribute) {
    Element element = doc.selectFirst(cssQuery);
    return (element != null) ? element.attr(attribute) : null;
  }

  private List<Integer> scrapeReviewsRating(Document document) throws IOException {
    List<Integer> ratings = new ArrayList<>();
    Elements reviewElements = document.select(".media.reply_list");

    for (Element reviewElement : reviewElements) {
      // 평점 추출 (이미지의 개수로 평점 계산)
      int starRating = reviewElement.select(".reply_list_star img[src='https://recipe1.ezmember.co.kr/img/mobile/icon_star2_on.png']").size();
      ratings.add(starRating);
    }

    return ratings; // 평점 리스트 반환
  }

}