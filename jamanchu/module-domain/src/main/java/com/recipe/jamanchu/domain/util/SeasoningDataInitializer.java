package com.recipe.jamanchu.domain.util;

import com.recipe.jamanchu.domain.entity.SeasoningEntity;
import com.recipe.jamanchu.domain.repository.SeasoningRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeasoningDataInitializer implements CommandLineRunner {

    private final SeasoningRepository seasoningRepository;

  @Override
  public void run(String... args) throws Exception {
    List<String> seasonings = List.of(
        "깨", "마늘", "생강", "파슬리", "간장", "겨자", "계피", "고추", "소스", "된장",
        "마요네즈", "맛술", "조미료", "가루", "드레싱", "소금", "식초", "쌈장", "케찹", "후추", "양념",
        "천일염", "참기름", "육수", "오일", "얼음", "액젓", "식용유", "설탕", "치노", "양파", "알롤로스",
        "물");

    for (String seasoning : seasonings) {
      if (!seasoningRepository.existsByName(seasoning)) {
        seasoningRepository.save(new SeasoningEntity(seasoning));
      }
    }
  }
}
