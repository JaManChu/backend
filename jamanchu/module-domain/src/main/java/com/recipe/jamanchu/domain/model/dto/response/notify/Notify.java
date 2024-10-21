package com.recipe.jamanchu.domain.model.dto.response.notify;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Notify {

  private final String recipeName;

  private final String message;

  private final Double rating;

  private final String commentUser;

  public static Notify of(String recipeName, String message, Double rating, String commentUser) {
    return new Notify(recipeName, message, rating, commentUser);
  }

}
