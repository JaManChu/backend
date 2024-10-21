package com.recipe.jamanchu.core.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PictureType {
  THUMBNAIL("thumbnail"),
  RECIPE_ORDER_IMAGE("recipe_order_image");

  private final String folderPrefix;
}
