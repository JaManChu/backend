package com.recipe.jamanchu.model.dto.request.comments;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentsUpdateDTO {

  @NotEmpty
  @Min(1)
  private final Long commentsId;

  @NotEmpty
  @Size(min = 1, max = 300)
  private final String comment;

  @DecimalMin(value = "1.0", inclusive = false)
  @DecimalMax(value = "5.0", inclusive = false)
  private final Double rating;

}
