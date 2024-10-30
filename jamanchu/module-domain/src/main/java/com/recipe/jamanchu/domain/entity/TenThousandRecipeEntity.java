package com.recipe.jamanchu.domain.entity;

import com.recipe.jamanchu.domain.model.type.CookingTimeType;
import com.recipe.jamanchu.domain.model.type.LevelType;
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
  private Long trId;

  @Column(name = "tr_name")
  private String trName;

  @Column(name = "tr_origin_id")
  private Long trOriginId;

  @Enumerated(EnumType.STRING)
  @Column(name = "tr_level")
  private LevelType trLevel;

  @Enumerated(EnumType.STRING)
  @Column(name = "tr_cook_time")
  private CookingTimeType trCookTime;

  @Column(name = "tr_rating")
  private Double trRating;

  @Column(name = "tr_review_count")
  private Integer trReviewCount;

  @Column(name = "tr_thumbnail")
  private String trThumbnail;

  @Column(name = "tr_ingredients", columnDefinition = "TEXT")
  private String trIngredients;

  @NotNull
  @Column(name = "tr_mn_contents", columnDefinition = "TEXT")
  private String trMnContents;

  @Column(name = "tr_mn_pictures", columnDefinition = "TEXT")
  private String trMnPictures;
}
