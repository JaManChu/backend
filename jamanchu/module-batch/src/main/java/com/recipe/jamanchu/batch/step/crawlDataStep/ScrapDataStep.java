package com.recipe.jamanchu.batch.step.crawlDataStep;

import com.recipe.jamanchu.batch.component.recipe.RecipeDivide;
import com.recipe.jamanchu.batch.component.recipe.ScrapTenThousandRecipe;
import com.recipe.jamanchu.domain.component.UserAccessHandler;
import com.recipe.jamanchu.domain.entity.RecipeEntity;
import com.recipe.jamanchu.domain.entity.TenThousandRecipeEntity;
import com.recipe.jamanchu.domain.entity.UserEntity;
import com.recipe.jamanchu.domain.model.dto.response.crawling.ScrapResult;
import com.recipe.jamanchu.domain.model.type.RecipeProvider;
import com.recipe.jamanchu.domain.repository.TenThousandRecipeRepository;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ScrapDataStep {

  private final JobRepository jobRepository;
  private final UserAccessHandler userAccessHandler;
  private final PlatformTransactionManager transactionManager;
  private final ScrapTenThousandRecipe scrapTenThousandRecipe;
  private final TenThousandRecipeRepository tenThousandRecipeRepository;
  private final RecipeDivide recipeDivide;

  @Bean
  public Step scrapData() {
    return new StepBuilder("scrapData", jobRepository)
        .<ScrapResult, TenThousandRecipeEntity>chunk(20, transactionManager) // 20개씩 처리
        .reader(scrapResultReader())
        .processor(scrapResultProcessor())
        .writer(scrapResultWriter())
        .build();
  }

  @Bean
  public ItemReader<ScrapResult> scrapResultReader() {
    List<ScrapResult> recipes = scrapTenThousandRecipe.getScrapedRecipes(200L); // 200개 데이터를 불러옵니다
    return new ItemReader<>() {
      private final Iterator<ScrapResult> iterator = recipes.iterator();
      @Override
      public ScrapResult read() {
        return iterator.hasNext() ? iterator.next() : null;
      }
    };
  }

  @Bean
  public ItemProcessor<ScrapResult, TenThousandRecipeEntity> scrapResultProcessor() {
    return scrapResult -> TenThousandRecipeEntity.builder()
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
        .build();
  }

  @Bean
  public ItemWriter<TenThousandRecipeEntity> scrapResultWriter() {
    return entitiesChunk -> {
      // Chunk를 List로 변환
      List<TenThousandRecipeEntity> filteredEntities = entitiesChunk.getItems().stream()
          .filter(Objects::nonNull)  // null 요소 필터링
          .collect(Collectors.toList());

      // 필터링된 리스트가 비어 있지 않은 경우에만 저장
      if (!filteredEntities.isEmpty()) {
        for (TenThousandRecipeEntity entity : filteredEntities) {
          saveEntityWithRollback(entity);
        }
        log.info(">>> {} 개 데이터 저장 성공", filteredEntities.size());
      } else {
        log.info(">>> List is null");
      }
    };
  }

  private RecipeEntity of(TenThousandRecipeEntity tenThousandRecipe) {
    UserEntity user = userAccessHandler.findByEmail("user@example.com");

    return RecipeEntity.builder()
        .user(user)
        .rcpName(tenThousandRecipe.getTrName())
        .rcpLevel(tenThousandRecipe.getTrLevel())
        .rcpTime(tenThousandRecipe.getTrCookTime())
        .rcpThumbnail(tenThousandRecipe.getTrThumbnail())
        .rcpProvider(RecipeProvider.SCRAP)
        .rcpOriginId(tenThousandRecipe.getTrOriginId())
        .build();
  }

  public void saveEntityWithRollback(TenThousandRecipeEntity entity) {
    TenThousandRecipeEntity tenThousandRecipe = tenThousandRecipeRepository.save(entity);
    RecipeEntity recipe = of(entity);
    recipeDivide.saveRecipeData(recipe);
    recipeDivide.saveRecipeRatingData(recipe, tenThousandRecipe);
    recipeDivide.saveManualData(recipe, tenThousandRecipe);
    recipeDivide.saveIngredientDetails(recipe, tenThousandRecipe);
    log.info("레시피 명 : {}", recipe.getRcpName());
  }
}
