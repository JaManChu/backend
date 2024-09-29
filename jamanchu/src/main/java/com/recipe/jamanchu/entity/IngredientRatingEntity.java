package com.recipe.jamanchu.entity;

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
@Table(name = "ingredient_rating")
public class IngredientRatingEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ir_id")
  private Long ingredientRatingId;

  // 유저와 재료 평가 테이블 연관 관계 매핑
  // 1 : N
  @NotNull
  @ManyToOne
  @JoinColumn(name = "usr_id")
  private UserEntity user;

  // 재료와 재료 평가 테이블 연관 관계 매핑
  // 1 : N
  @NotNull
  @ManyToOne
  @JoinColumn(name = "ing_id")
  private IngredientEntity ingredient;

  @NotNull
  @Column(name = "ir_rating")
  private Double rating;

  @NotNull
  @Column(name = "ir_point", columnDefinition = "double default 1.0")
  private Double point;

}
