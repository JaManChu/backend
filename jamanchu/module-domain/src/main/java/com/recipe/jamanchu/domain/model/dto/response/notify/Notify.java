package com.recipe.jamanchu.domain.model.dto.response.notify;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Notify {

  private final Long recipeId;

  private final String recipeName;

  private final String message;

  private final Double rating;

  private final String commentUser;

  public static Notify of(Long recipeId, String recipeName, String message, Double rating, String commentUser) {
    return new Notify(recipeId, recipeName, message, rating, commentUser);
  }

}
