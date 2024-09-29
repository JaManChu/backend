package com.recipe.jamanchu.entity;

import com.recipe.jamanchu.util.RecipeIngredientMappingId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
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
@Table(name = "rcp_ing_mapping")
@IdClass(RecipeIngredientMappingId.class)
public class RecipeIngredientMappingEntity {

  @Id
  @ManyToOne
  @NotNull
  @JoinColumn(name="rcp_id")
  private RecipeEntity recipe;

  @Id
  @ManyToOne
  @NotNull
  @JoinColumn(name="ing_id")
  private IngredientEntity ingredient;

}
