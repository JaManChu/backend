package com.recipe.jamanchu.model.dto.response.recipes;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecipesOrders {

  private final List<RecipesOrder> recipesOrders;

}
