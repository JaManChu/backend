package com.recipe.jamanchu.batch.component.recipe;

import com.recipe.jamanchu.domain.model.dto.response.crawling.ScrapResult;
import com.recipe.jamanchu.domain.model.type.CookingTimeType;
import com.recipe.jamanchu.domain.model.type.LevelType;
import com.recipe.jamanchu.domain.repository.TenThousandRecipeRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class ScrapTenThousandRecipe {

  private static final String url = "https://www.10000recipe.com/recipe/";
  private final TenThousandRecipeRepository tenThousandRecipeRepository;

  // 지정된 수만큼의 레시피 데이터를 크롤링하여 ScrapResult 리스트로 반환
  public List<ScrapResult> getScrapedRecipes(Long maxRecipes) {
    Long lastRecipeId = tenThousandRecipeRepository.findMaxTrOriginId();
    List<ScrapResult> recipeBatch = new ArrayList<>();

    // 레시피 ID 범위에 따라 반복하여 데이터 크롤링
    for (long recipeId = lastRecipeId + 1; recipeId <= lastRecipeId + maxRecipes; recipeId++) {
      String recipeUrl = url + recipeId;
      ScrapResult result = scrapeRecipeDetails(recipeUrl);
      if (result != null) {
        recipeBatch.add(result);
      }
    }
    return recipeBatch;
  }

  // 주어진 URL에서 레시피 세부 정보를 크롤링하여 ScrapResult로 반환
  private ScrapResult scrapeRecipeDetails(String url) {
    try {
      // 레시피 상세 페이지 파싱
      Document recipeDoc = Jsoup.connect(url).get();

      // 레시피 제목 추출
      String title = getText(recipeDoc, ".view2_summary h3");
      if (title == null) return null;

      // 조리 순서 및 이미지 추출
      Elements stepsElements = recipeDoc.select(".view_step_cont");
      Elements stepImagesElements = recipeDoc.select(".view_step_cont img");

      if (stepsElements.isEmpty()) return null;

      StringBuilder manualContents = new StringBuilder();
      StringBuilder manualPictures = new StringBuilder();

      // 조리 단계 텍스트 추출
      for (Element step : stepsElements) manualContents.append(step.text()).append("$%^");

      // 조리 단계 이미지 URL 추출
      for (Element image : stepImagesElements) manualPictures.append(image.attr("src")).append(",");

      // 재료 정보 추출
      Element ingredientElement = recipeDoc.selectFirst("#divConfirmedMaterialArea");
      StringBuilder ingredients = new StringBuilder();
      if (ingredientElement != null) {
        Elements ingredientLists = ingredientElement.select("ul");
        for (Element ingredientList : ingredientLists) {
          Elements items = ingredientList.select("li");
          for (Element item : items) {
            // 재료 이름 및 양 추출
            String ingredientName = item.selectFirst(".ingre_list_name").text();
            String ingredientAmount = item.selectFirst(".ingre_list_ea").text();
            ingredients.append(ingredientName).append(" ").append(ingredientAmount).append(",");
          }
        }
      } else {
        return null;  // 재료 정보가 없으면 null 반환
      }

      // recipeId 추출
      String[] urlParts = url.split("/");
      Long recipeId = Long.parseLong(urlParts[urlParts.length - 1]);

      // 난이도 추출
      String level = getText(recipeDoc, ".view2_summary_info3");
      LevelType levelType = LevelType.fromString(level);

      // 조리 시간 추출
      String cookTime = getText(recipeDoc, ".view2_summary_info2");
      CookingTimeType cookingTimeType = CookingTimeType.fromString(cookTime);

      // 썸네일 이미지 URL 추출
      String thumbnail = getAttr(recipeDoc, ".centeredcrop img", "src");

      // 평점 계산
      List<Integer> reviewsRating = scrapeReviewsRating(recipeDoc);
      int reviewCount = reviewsRating.size();
      double averageRating = reviewsRating.stream().mapToInt(Integer::intValue).average().orElse(0.0);
      averageRating = Math.round(averageRating * 100.0) / 100.0;

      // ScrapResult 객체 반환
      return new ScrapResult(title, recipeId, levelType, cookingTimeType, thumbnail, averageRating, reviewCount,
          ingredients.toString(), manualContents.toString(), manualPictures.toString());

    } catch (IOException e) {
      log.error("에러 발생 URL {}: {}", url, e.getMessage());
    }
    return null;  // 에러 발생 시 null 반환
  }

  // 주어진 CSS 쿼리를 통해 텍스트 추출
  private String getText(Document doc, String cssQuery) {
    Element element = doc.selectFirst(cssQuery);
    return (element != null) ? element.text() : null;
  }

  // 주어진 CSS 쿼리로 속성 값 추출
  private String getAttr(Document doc, String cssQuery, String attribute) {
    Element element = doc.selectFirst(cssQuery);
    return (element != null) ? element.attr(attribute) : null;
  }

  // 레시피 페이지에서 평점 리스트 추출
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