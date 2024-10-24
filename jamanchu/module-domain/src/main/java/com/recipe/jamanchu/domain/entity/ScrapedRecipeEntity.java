package com.recipe.jamanchu.domain.entity;

import com.recipe.jamanchu.domain.model.type.ScrapedType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "scraped_recipe")
public class ScrapedRecipeEntity extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "sr_id")
  private Long scrapedRecipeId;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "rcp_id")
  private RecipeEntity recipe;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "usr_id")
  private UserEntity user;

  @NotNull
  @Enumerated(EnumType.STRING)
  private ScrapedType scrapedType;

  public void updateScrapedType(ScrapedType scrapedType) {
    this.scrapedType = scrapedType;
  }
}
