package com.recipe.jamanchu.entity;

import com.recipe.jamanchu.model.type.CookingTimeType;
import com.recipe.jamanchu.model.type.LevelType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "ten_recipe")
public class TenThousandRecipeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "tr_id")
  private Long crawledRecipeId;

  @Column(name = "tr_name")
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "tr_level")
  private LevelType levelType;

  @Enumerated(EnumType.STRING)
  @Column(name = "tr_cook_time")
  private CookingTimeType cookingTimeType;

  @Column(name = "tr_rating")
  private Double rating;

  @Column(name = "tr_review_count")
  private Integer crReviewCount;

  @Column(name = "tr_thumbnail")
  private String thumbnail;

  @Column(name = "tr_ingredients")
  private String ingredients;

  @NotNull
  @Column(name = "tr_mn_contents", columnDefinition = "TEXT")
  private String crManualContents;

  @Column(name = "tr_mn_pictures", columnDefinition = "TEXT")
  private String crManualPictures;
}
