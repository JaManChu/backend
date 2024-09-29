package com.recipe.jamanchu.util;

import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class RecipeIngredientMappingId implements Serializable {

  private Long recipe;

  private Long ingredient;

  @Override
  public boolean equals(Object o){

    if( this == o ) return true;

    if( o == null || getClass() != o.getClass() ) return false;

    RecipeIngredientMappingId that = (RecipeIngredientMappingId) o;

    return Objects.equals(recipe, that.recipe) && Objects.equals(ingredient, that.ingredient);
  }

  @Override
  public int hashCode(){
    return Objects.hash(recipe, ingredient);
  }
}
