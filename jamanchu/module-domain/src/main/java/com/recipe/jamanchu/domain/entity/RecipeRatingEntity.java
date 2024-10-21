package com.recipe.jamanchu.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "rcp_rating")
public class RecipeRatingEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "rr_id")
  private Long recipeRatingId;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "rcp_id")
  private RecipeEntity recipe;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "usr_id")
  private UserEntity user;

  @NotNull
  @Column(name = "rr_rating", columnDefinition = "double default 1.0")
  private Double rating;

}
