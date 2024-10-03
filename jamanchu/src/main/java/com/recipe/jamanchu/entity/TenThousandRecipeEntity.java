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
  @Column(name = "cr_id")
  private Long crawledRecipeId;

  @Column(name = "cr_name")
  private String name;

  @Column(name = "cr_author")
  private String authorName;

  @Enumerated(EnumType.STRING)
  @Column(name = "cr_level")
  private LevelType levelType;

  @Enumerated(EnumType.STRING)
  @Column(name = "cr_cook_time")
  private CookingTimeType cookingTimeType;

  @Column(name = "cr_rating")
  private Double rating;

  @Column(name = "cr_review_count")
  private Integer crReviewCount;

  @Column(name = "cr_thumbnail")
  private String thumbnail;

  @Column(name = "cr_ingredients")
  private String ingredients;

  @NotNull
  @Column(name = "cr_mn_content", columnDefinition = "TEXT")
  private String crManualContent;

  @Column(name = "cr_mn_picture", columnDefinition = "TEXT")
  private String crManualPicture;
}
