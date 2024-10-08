package com.recipe.jamanchu.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@Component
public class LastRecipeIdUtil {
  private Long lastRecipeId;
}
