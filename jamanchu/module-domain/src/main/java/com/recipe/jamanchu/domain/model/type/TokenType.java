package com.recipe.jamanchu.domain.model.type;

import lombok.Getter;

@Getter
public enum TokenType {
  ACCESS("access-token"),
  REFRESH("_refresh-token");

  private final String value;

  TokenType(String value) {
    this.value = value;
  }
}