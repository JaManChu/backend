package com.recipe.jamanchu.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "recommend_rcp")
@ToString
public class RecommendRecipeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long recId;

  @ManyToOne
  @JoinColumn(name = "usr_id")
  @ToString.Exclude
  private UserEntity user;

  @ManyToOne
  @JoinColumn(name = "rcp_id")
  @ToString.Exclude
  private RecipeEntity recipe;

  private Double recRating;

}
