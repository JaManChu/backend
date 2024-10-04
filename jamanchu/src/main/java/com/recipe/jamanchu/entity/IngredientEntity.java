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
@Table(name = "rcp_ing")
public class IngredientEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ing_id")
  private Long ingredientId;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "rcp_id")
  private RecipeEntity recipe;

  @NotNull
  @Column(name = "ing_name", length = 30)
  private String name;

  @Column(name = "quantity")
  private String quantity;
}
